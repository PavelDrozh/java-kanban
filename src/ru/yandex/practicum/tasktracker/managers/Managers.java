package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.converters.FileToDataConverter;
import ru.yandex.practicum.tasktracker.data.Task;

import java.io.File;
import java.net.URI;
import java.util.List;

public final class Managers {

    private Managers() {

    }

    public static final URI MANAGERS_URI = URI.create("http://localhost:8078/");

    public static HttpTaskManager getDefault() {
        return new HttpTaskManager(MANAGERS_URI);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static FileBackedTaskManager getDefaultFileBacked() {
        return new FileBackedTaskManager();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        List<Task> tasks = FileToDataConverter.getTasksFromFile(file);
        List<Integer> history = FileToDataConverter.getHistoryFromFile(file);
        FileBackedTaskManager tasksManager = new FileBackedTaskManager();
        for (Task task : tasks) {
            tasksManager.loadTask(task);
        }
        for (Integer id : history) {
            tasksManager.getTaskOrNull(id);
            tasksManager.getSubtaskOrNull(id);
            tasksManager.getEpicOrNull(id);
        }
        return tasksManager;
    }

    public static HttpTaskManager loadFromServer(URI uri) {
        HttpTaskManager tasksManager = new HttpTaskManager(uri);
        tasksManager.load();
        return tasksManager;
    }
}
