package ru.yandex.practicum.tasktracker.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;
import ru.yandex.practicum.tasktracker.managers.HttpTaskManager;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class TasksHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final String NO_SUCH_TASKS = "No such tasks";
    private static final String ID_REGEX = "id=";
    private static final int ERROR_CODE = 404;
    private static final int SUCCESS_CODE = 200;
    private static final int CREATED_CODE = 201;
    private static final int BAD_REQUEST_CODE = 400;
    private final HttpTaskManager manager;
    private final Gson gson;


    public TasksHandler(HttpTaskManager manager) {
        this.manager = manager;
        gson = new Gson();
    }

    @Override
    public void handle(HttpExchange httpExchange) {
        try {
            String response = "";
            String method = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String params = httpExchange.getRequestURI().getQuery();
            int code = ERROR_CODE;

            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        int id = parseParameterId(params);
                        if (id != -1 && manager.getTaskOrNull(id) != null) {
                            Task task = manager.getTaskOrNull(id);
                            response = gson.toJson(task);
                            code = SUCCESS_CODE;
                        } else {
                            response = NO_SUCH_TASKS;
                            code = BAD_REQUEST_CODE;
                        }
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        int id = parseParameterId(params);
                        if (id != -1 && manager.getSubtaskOrNull(id) != null) {
                            Task task = manager.getSubtaskOrNull(id);
                            response = gson.toJson(task);
                            code = SUCCESS_CODE;
                        } else {
                            response = NO_SUCH_TASKS;
                            code = BAD_REQUEST_CODE;
                        }
                        break;

                    }
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        int id = parseParameterId(params);
                        if (id != -1 && manager.getEpicOrNull(id) != null) {
                            Task task = manager.getEpicOrNull(id);
                            response = gson.toJson(task);
                            code = SUCCESS_CODE;
                        } else {
                            response = NO_SUCH_TASKS;
                            code = BAD_REQUEST_CODE;
                        }
                        break;
                    }
                    if (Pattern.matches("^/tasks$", path)) {
                        List<Task> tasks = manager.getPrioritizedTasks();
                        response = gson.toJson(tasks);
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks/task$", path)) {
                        List<Task> tasks = manager.getTasksList();
                        response = gson.toJson(tasks);
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        List<Subtask> tasks = manager.getSubtasksList();
                        response = gson.toJson(tasks);
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        List<Epic> tasks = manager.getEpicsList();
                        response = gson.toJson(tasks);
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/$", path)) {
                        int id = parseParameterId(params);
                        if (id != -1) {
                            Epic epic = manager.getEpicOrNull(id);
                            if (epic != null) {
                                List<Subtask> subtasks = new ArrayList<>(epic.getSubtasks().values());
                                response = gson.toJson(subtasks);
                            } else {
                                response = NO_SUCH_TASKS;
                            }
                        } else {
                            response = NO_SUCH_TASKS;
                        }
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks/history$", path)) {
                        List<Task> tasks = manager.getHistory();
                        response = gson.toJson(tasks);
                        code = SUCCESS_CODE;
                        break;
                    }
                    break;
                case "POST":
                    InputStream inputStream1 = httpExchange.getRequestBody();
                    String body = new String(inputStream1.readAllBytes(), DEFAULT_CHARSET);
                    if (Pattern.matches("^/tasks/task$", path)) {
                        Task task = gson.fromJson(body, Task.class);
                        Task created = null;
                        boolean isUpdate = manager.updateTask(task);
                        if (!isUpdate) {
                            created = manager.createTask(task);
                            response = gson.toJson(created);
                        }
                        if (!isUpdate && created == null) {
                            code = BAD_REQUEST_CODE;
                        } else {
                            code = CREATED_CODE;
                            response = gson.toJson(task);
                        }
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        Subtask task = gson.fromJson(body, Subtask.class);
                        Subtask created = null;
                        boolean isUpdate = manager.updateTask(task);
                        if (!isUpdate) {
                            created = (Subtask) manager.createTask(task);
                            response = gson.toJson(created);
                        }
                        if (!isUpdate && created == null) {
                            code = BAD_REQUEST_CODE;
                        } else {
                            code = CREATED_CODE;
                            response = gson.toJson(task);
                        }
                        break;
                    }
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        Epic task = gson.fromJson(body, Epic.class);
                        Epic created = null;
                        boolean isUpdate = manager.updateTask(task);
                        if (!isUpdate) {
                            created = (Epic) manager.createTask(task);
                            response = gson.toJson(created);
                        }
                        if (!isUpdate && created == null) {
                            code = BAD_REQUEST_CODE;
                        } else {
                            code = CREATED_CODE;
                            response = gson.toJson(task);
                        }
                        break;
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/task/$", path)) {
                        int id = parseParameterId(params);
                        if (id != -1 && manager.getTaskOrNull(id) != null) {
                            Task task = manager.deleteTaskOrNull(id);
                            response = gson.toJson(task);
                            code = SUCCESS_CODE;
                        } else {
                            response = NO_SUCH_TASKS;
                            code = BAD_REQUEST_CODE;
                        }
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask/$", path)) {
                        int id = parseParameterId(params);
                        if (id != -1 && manager.getSubtaskOrNull(id) != null) {
                            Subtask task = manager.deleteSubtaskOrNull(id);
                            response = gson.toJson(task);
                            code = SUCCESS_CODE;
                        } else {
                            response = NO_SUCH_TASKS;
                            code = BAD_REQUEST_CODE;
                        }
                        break;
                    }
                    if (Pattern.matches("^/tasks/epic/$", path)) {
                        int id = parseParameterId(params);
                        if (id != -1 && manager.getEpicOrNull(id) != null) {
                            Epic task = manager.deleteEpicOrNull(id);
                            response = gson.toJson(task);
                            code = SUCCESS_CODE;
                        } else {
                            response = NO_SUCH_TASKS;
                            code = BAD_REQUEST_CODE;
                        }
                        break;
                    }
                    if (Pattern.matches("^/tasks/task$", path)) {
                        manager.deleteTasks();
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        manager.deleteSubtasks();
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        manager.deleteEpics();
                        code = SUCCESS_CODE;
                        break;
                    }
                    if (Pattern.matches("^/tasks$", path)) {
                        manager.deleteAllTasks();
                        code = SUCCESS_CODE;
                        break;
                    }
                    break;
                default:
                    response = "Некорректный метод!";
            }

            httpExchange.sendResponseHeaders(code, response.getBytes().length);
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(response.getBytes());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private int parseParameterId(String str) {
        try {
            return Integer.parseInt(str.replaceFirst(ID_REGEX, ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
