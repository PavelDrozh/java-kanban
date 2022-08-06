package ru.yandex.practicum.tasktracker;

import java.util.List;

public interface TaskManager {

    List<Task> getTasksList();

    List<Epic> getEpicsList();

    List<Subtask> getSubtasksList();

    void createTask(Task task);

    boolean updateTask(Task task);

    void deleteAllTasks();

    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    Task getTaskOrNull(int id);

    Task deleteTaskOrNull(int id);

    List<Subtask> getTasksByEpicId (int id);

    List<Task> getHistory();
}
