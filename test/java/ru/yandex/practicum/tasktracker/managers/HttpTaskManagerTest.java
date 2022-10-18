package ru.yandex.practicum.tasktracker.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;
import ru.yandex.practicum.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.tasktracker.servers.HttpTaskServer;
import ru.yandex.practicum.tasktracker.servers.KVServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager>{

    private static final String BASE_URL = "http://localhost:8080/";
    public static final String PRIORITY_TASKS_PATH = "tasks";
    private static final String TASKS_PATH = PRIORITY_TASKS_PATH + "/task";
    private static final String SUBTASKS_PATH = PRIORITY_TASKS_PATH + "/subtask";
    private static final String EPICS_PATH = PRIORITY_TASKS_PATH + "/epic";
    public static final String TASKS_HISTORY_PATH = PRIORITY_TASKS_PATH + "/history";
    public static final String SUBTASKS_OF_EPIC_PATH = SUBTASKS_PATH + "/epic";
    private static final String ID_PARAMETER = "/?id=";

    private KVServer kvServer;
    private HttpTaskServer taskServer;
    private final Gson gson = new Gson();


    @BeforeEach
    void createManager() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
        manager = Managers.getDefault();
        taskServer = new HttpTaskServer(manager);
    }

    @AfterEach
    void stop() {
        kvServer.stop();
        taskServer.stop();
    }

    @Test
    void getHttpFromEmptyServer() {
        HttpTaskManager realManger = Managers.loadFromServer(Managers.MANAGERS_URI);
        assertTrue(realManger.getTasksList().isEmpty());
        assertTrue(realManger.getEpicsList().isEmpty());
        assertTrue(realManger.getSubtasksList().isEmpty());
        assertTrue(realManger.getHistory().isEmpty());
    }

    @Test
    void getHttpFromNormalServer() {
        normalFilling();
        manager.getTaskOrNull(1);
        List<Task> expectedTasks = manager.getTasksList();
        List<Epic> expectedEpic = manager.getEpicsList();
        List<Subtask> expectedSubtasks = manager.getSubtasksList();
        List<Integer> expectedHistory = new ArrayList<>();
        manager.getHistory().stream().mapToInt(Task::getId).forEach(expectedHistory::add);
        assertFalse(expectedTasks.isEmpty());
        assertFalse(expectedEpic.isEmpty());
        assertFalse(expectedSubtasks.isEmpty());
        assertFalse(expectedHistory.isEmpty());
        HttpTaskManager realManger = Managers.loadFromServer(Managers.MANAGERS_URI);
        List<Task> realTasks = realManger.getTasksList();
        List<Epic> realEpic = realManger.getEpicsList();
        List<Subtask> realSubtasks = realManger.getSubtasksList();
        List<Integer> realHistory = new ArrayList<>();
        realManger.getHistory().stream().mapToInt(Task::getId).forEach(realHistory::add);
        assertIterableEquals(expectedTasks, realTasks);
        assertIterableEquals(expectedEpic, realEpic);
        assertIterableEquals(expectedSubtasks, realSubtasks);
        assertIterableEquals(expectedHistory, realHistory);
    }

    @Test
    void getHttpFromNotExistingServer() {
        ManagerSaveException ex = getManagerSaveException("http://localhost:8070/");
        assertEquals("Ошибка при подключении к KVServer", ex.getMessage());
    }

    private ManagerSaveException getManagerSaveException(String s) {
        URI uri = URI.create(s);
        return assertThrows(ManagerSaveException.class,
                () -> Managers.loadFromServer(uri));
    }

    @Test
    void mustCreateTasksWithHttpRequest() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest taskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        client.send(taskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest epicCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        client.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest subtaskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();
        client.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getTasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH + ID_PARAMETER + 1))
                .GET()
                .build();
        HttpRequest getEpicsRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH + ID_PARAMETER + 2))
                .GET()
                .build();
        HttpRequest getSubtasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH + ID_PARAMETER + 3))
                .GET()
                .build();

        Task tasksResponse = gson.fromJson(
                client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                Task.class
        );

        Epic epicsResponse = gson.fromJson(
                client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString()).body(),
                Epic.class
        );

        Subtask subtasksResponse = gson.fromJson(
                client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                Subtask.class
        );

        Epic expectedEpic = getNewEpic(expectedSubtask1);

        assertEquals(expectedTask1, tasksResponse);
        assertEquals(expectedEpic, epicsResponse);
        assertEquals(expectedSubtask1, subtasksResponse);
    }

    @Test
    void mustCreateAndDeleteAllTasksTypes() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest taskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        client.send(taskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest epicCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        client.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest subtaskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();
        client.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteTaskRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH + ID_PARAMETER + 1))
                .DELETE()
                .build();
        client.send(deleteTaskRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteSubtaskRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH + ID_PARAMETER + 3))
                .DELETE()
                .build();
        client.send(deleteSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteEpicRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH + ID_PARAMETER + 2))
                .DELETE()
                .build();
        client.send(deleteEpicRequest, HttpResponse.BodyHandlers.ofString());


        HttpRequest getTasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .GET()
                .build();
        HttpRequest getEpicsRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .GET()
                .build();
        HttpRequest getSubtasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH))
                .GET()
                .build();

        List<Task> tasksResponse = gson.fromJson(
                client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Task>>() {}.getType()
        );

        List<Epic> epicsResponse = gson.fromJson(
                client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Epic>>() {}.getType()
        );

        List<Subtask> subtasksResponse = gson.fromJson(
                client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Subtask>>() {}.getType()
        );

        assertEquals(0, tasksResponse.size());
        assertEquals(0, epicsResponse.size());
        assertEquals(0, subtasksResponse.size());
    }

    @Test
    void mustCreateAndUpdateAllTasksTypes() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest taskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(task1)))
                .build();
        client.send(taskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest epicCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(epic1)))
                .build();
        client.send(epicCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest subtaskCreateRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(subtask1)))
                .build();
        client.send(subtaskCreateRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest updateTaskRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask1)))
                .build();
        client.send(updateTaskRequest, HttpResponse.BodyHandlers.ofString());

        Epic newEpic = getNewEpic(newSubtask1);
        HttpRequest updateEpicRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newEpic)))
                .build();
        client.send(updateEpicRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest getTasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .GET()
                .build();
        HttpRequest getEpicsRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .GET()
                .build();
        HttpRequest getSubtasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH))
                .GET()
                .build();

        List<Task> tasksResponse = gson.fromJson(
                client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Task>>() {}.getType()
        );

        List<Epic> epicsResponse = gson.fromJson(
                client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Epic>>() {}.getType()
        );

        List<Subtask> subtasksResponse = gson.fromJson(
                client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Subtask>>() {}.getType()
        );

        assertEquals(1, tasksResponse.size());
        assertEquals(1, epicsResponse.size());
        assertEquals(1, subtasksResponse.size());
        assertEquals(newTask1, tasksResponse.get(0));
        assertEquals(newEpic, epicsResponse.get(0));
        assertEquals(newSubtask1, subtasksResponse.get(0));

    }
    @Test
    void mustDeleteAllTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        normalFilling();
        HttpRequest deleteTaskRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .DELETE()
                .build();
        client.send(deleteTaskRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteSubtaskRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH))
                .DELETE()
                .build();
        client.send(deleteSubtaskRequest, HttpResponse.BodyHandlers.ofString());

        HttpRequest deleteEpicRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .DELETE()
                .build();
        client.send(deleteEpicRequest, HttpResponse.BodyHandlers.ofString());


        HttpRequest getTasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_PATH))
                .GET()
                .build();
        HttpRequest getEpicsRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + EPICS_PATH))
                .GET()
                .build();
        HttpRequest getSubtasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_PATH))
                .GET()
                .build();

        List<Task> tasksResponse = gson.fromJson(
                client.send(getTasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Task>>() {}.getType()
        );

        List<Epic> epicsResponse = gson.fromJson(
                client.send(getEpicsRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Epic>>() {}.getType()
        );

        List<Subtask> subtasksResponse = gson.fromJson(
                client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Subtask>>() {}.getType()
        );

        assertEquals(0, tasksResponse.size());
        assertEquals(0, epicsResponse.size());
        assertEquals(0, subtasksResponse.size());
    }

    @Test
    void mustReturnHistory() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        normalFilling();
        manager.getTaskOrNull(1);
        manager.getSubtaskOrNull(3);
        manager.getEpicOrNull(2);
        HttpRequest getHistoryRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + TASKS_HISTORY_PATH))
                .GET()
                .build();
        List<Task> historyResponse = gson.fromJson(
                client.send(getHistoryRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Task>>() {}.getType()
        );
        final List<Integer> history = historyResponse.stream()
                                                        .mapToInt(Task::getId)
                                                        .boxed()
                                                        .collect(Collectors.toList());
        List<Integer> expectedHistory = List.of(1,3,2);
        assertIterableEquals(expectedHistory, history);
    }

    @Test
    void mustReturnPriority() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        normalFilling();
        HttpRequest getPriorityRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + PRIORITY_TASKS_PATH))
                .GET()
                .build();
        List<Task> historyResponse = gson.fromJson(
                client.send(getPriorityRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Task>>() {}.getType()
        );
        List<Integer> priority = historyResponse.stream()
                .mapToInt(Task::getId)
                .boxed()
                .collect(Collectors.toList());
        List<Integer> expectedPriority = List.of(1,3,5,6);
        assertIterableEquals(expectedPriority, priority);
    }

    @Test
    void mustReturnEpicsSubtasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        normalFilling();
        HttpRequest getSubtasksRequest = HttpRequest
                .newBuilder()
                .uri(URI.create(BASE_URL + SUBTASKS_OF_EPIC_PATH + ID_PARAMETER + 2))
                .GET()
                .build();
        List<Subtask> subtasksResponse = gson.fromJson(
                client.send(getSubtasksRequest, HttpResponse.BodyHandlers.ofString()).body(),
                new TypeToken<List<Subtask>>() {}.getType()
        );

        assertEquals(expectedSubtask1, subtasksResponse.get(0));
    }
}
