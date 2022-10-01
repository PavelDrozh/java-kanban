package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;

import java.util.List;

public interface TaskManager {

    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<Subtask> getSubtasksList();

    Task createTask(Task task);

    boolean updateTask(Task task);

    void deleteAllTasks();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTaskOrNull(int id);

    Task deleteTaskOrNull(int id);

    List<Subtask> getTasksByEpicId (int id);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
