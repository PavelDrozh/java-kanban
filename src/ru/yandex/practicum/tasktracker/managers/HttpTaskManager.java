package ru.yandex.practicum.tasktracker.managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;
import ru.yandex.practicum.tasktracker.exceptions.ManagerSaveException;
import ru.yandex.practicum.tasktracker.servers.KVTaskClient;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class HttpTaskManager extends FileBackedTaskManager {
    private final Gson gson = new Gson();
    private final KVTaskClient taskClient;

    public HttpTaskManager(URI uri) {
        super();
        try {
            taskClient = new KVTaskClient(uri);
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при подключении к KVServer");
        }
    }

    public void load() {
        try {
            List<Task> tasks = gson.fromJson(
                    taskClient.load("tasks"),
                    new TypeToken<List<Task>>() {
                    }.getType()
            );
            List<Epic> epics = gson.fromJson(
                    taskClient.load("epics"),
                    new TypeToken<List<Epic>>() {
                    }.getType()
            );
            List<Subtask> subtasks = gson.fromJson(
                    taskClient.load("subtasks"),
                    new TypeToken<List<Subtask>>() {
                    }.getType()
            );
            List<Task> historyList = gson.fromJson(
                    taskClient.load("history"),
                    new TypeToken<List<Task>>() {
                    }.getType()
            );
            HistoryManager history = super.historyManager();
            if (historyList != null) historyList.forEach(history::add);
            List<Task> all = new ArrayList<>();
            if (tasks != null) all.addAll(tasks);
            if (epics != null) all.addAll(epics);
            if (subtasks != null) all.addAll(subtasks);
            all.sort(Comparator.comparingInt(Task::getId));
            all.forEach(super::loadTask);

        } catch (IOException | InterruptedException exception) {
            System.out.println("Ошибка при восстановлении данных");
        }
    }

    @Override
    protected void save() {
        try {
            taskClient.put("tasks", gson.toJson(super.getTasksList()));
            taskClient.put("epics", gson.toJson(super.getEpicsList()));
            taskClient.put("subtasks", gson.toJson(super.getSubtasksList()));
            taskClient.put("history", gson.toJson(super.getHistory()));
        } catch (IOException | InterruptedException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных");
        }
    }
}
