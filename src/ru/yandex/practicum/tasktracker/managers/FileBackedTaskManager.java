package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.converters.DataToFileConverter;
import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final Map<Integer, Task> allTasks;

    public FileBackedTaskManager() {
        super();
        allTasks = new TreeMap<>();
    }

    public void loadTask(Task task) {
        if (super.tasksId < task.getId()) {
            super.tasksId = task.getId() - 1;
            task.setId(-1);
        }
        createTask(task);
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        if (created != null) {
            allTasks.put(created.getId(), created);
            save();
        }
        return created;
    }

    private void save() {
        DataToFileConverter.writeFile(new ArrayList<>(allTasks.values()), super.historyManager());
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = super.updateTask(task);
        if (result) {
            allTasks.put(task.getId(), task);
            save();
        }
        return result;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        allTasks.clear();
        save();
    }

    @Override
    public void deleteTasks() {
        List<Task> taskList = super.getTasksList();
        for (Task task : taskList) {
            allTasks.remove(task.getId());
        }
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        List<Epic> epics = super.getEpicsList();
        List<Subtask> subtasks = super.getSubtasksList();
        for (Epic epic : epics) {
            allTasks.remove(epic.getId());
        }
        for (Subtask subtask : subtasks) {
            allTasks.remove(subtask.getId());
        }
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        List<Subtask> subtasks = super.getSubtasksList();
        for (Subtask subtask : subtasks) {
            allTasks.remove(subtask.getId());
        }
        super.deleteSubtasks();
        save();
    }

    @Override
    public Task getTaskOrNull(int id) {
        Task toGet = super.getTaskOrNull(id);
        if (toGet != null) {
            save();
        }
        return toGet;
    }

    @Override
    public Task deleteTaskOrNull(int id) {
        Task toDelete = super.deleteTaskOrNull(id);
        if (toDelete != null) {
            allTasks.remove(toDelete.getId());
            save();
        }
        return toDelete;
    }
}
