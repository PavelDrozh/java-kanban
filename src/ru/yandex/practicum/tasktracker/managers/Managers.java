package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.converters.FileToDataConverter;
import ru.yandex.practicum.tasktracker.data.Task;

import java.io.File;
import java.util.List;

public final class Managers {

    private Managers() {

    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
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
        }
        return tasksManager;
    }
}
