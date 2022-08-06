package ru.yandex.practicum.tasktracker;

import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;

public class InMemoryTaskManager implements TaskManager {

    private static final int FIRST_INDEX = 0;
    private final Map<Integer,Task> tasks;
    private final Map<Integer,Epic> epics;
    private final Map<Integer,Subtask> subtasks;
    private final HistoryManager history;
    private int tasksId;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistory();
        tasksId = FIRST_INDEX;
    }

    @Override
    public List<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void createTask(Task task) {
        task.setId(tasksId);
        tasksId++;
        if (task.getClass().equals(Subtask.class)) {
            createSubtask((Subtask) task);
        } else if (task.getClass().equals(Epic.class)) {
            crateEpic((Epic) task);
        } else {
            tasks.put(task.getId(), task);
        }
    }

    private void crateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    private void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpic();
        if (epics.containsKey(epicId)) {
            Epic managersEpic = epics.get(epicId);
            managersEpic.addSubtask(subtask);
            subtasks.put(subtask.getId(), subtask);
        }
    }

    @Override
    public boolean updateTask(Task task) {
        boolean result = false;
        if (tasks.containsKey(task.getId()) && task.getClass().equals(Task.class)) {
            tasks.put(task.getId(), task);
            result = true;
        } else if (epics.containsKey(task.getId()) && task.getClass().equals(Epic.class)) {
            epics.put(task.getId(), (Epic) task);
            result = true;
        } else if (subtasks.containsKey(task.getId()) && task.getClass().equals(Subtask.class)) {
            result = updateSubtask((Subtask) task);
        }
        return result;
    }

    private boolean updateSubtask(Subtask task) {
        boolean result = false;
        int epicId = task.getEpic();
        if (epics.containsKey(epicId)) {
            Epic managersEpic = epics.get(epicId);
            if (managersEpic.getSubtasks().containsKey(task.getId())) {
                managersEpic.addSubtask(task);
                subtasks.put(task.getId(), task);
                result = true;
            }
        }
        return result;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.deleteSubtasks();
        }
    }

    @Override
    public Task getTaskOrNull(int id) {
        Task result = null;
        if (tasks.containsKey(id)) {
            result = tasks.get(id);
        } else if (epics.containsKey(id)) {
            result = epics.get(id);
        } else if (subtasks.containsKey(id)) {
            result = subtasks.get(id);
        }
        history.add(result);
        return result;
    }

    @Override
    public Task deleteTaskOrNull(int id) {
        Task deleted = null;
        if (tasks.containsKey(id)) {
            deleted = tasks.remove(id);
        } else if (epics.containsKey(id)) {
            deleted = deleteEpic(id);
        } else if (subtasks.containsKey(id)) {
            deleted = deleteSubtask(id);
        }
        return deleted;
    }

    private Task deleteEpic(int id) {
        Task deleted;
        Epic epicToDelete = epics.get(id);
        Set<Integer> subtasksId = epicToDelete.getSubtasks().keySet();

        for (Integer subtaskId : subtasksId) {
            subtasks.remove(subtaskId);
        }
        deleted = epics.remove(id);
        return deleted;
    }

    private Subtask deleteSubtask(int id) {
        Subtask deleted = null;
        for (Epic epic : epics.values()) {
            if (epic.getSubtasks().containsKey(id)) {
                subtasks.remove(id);
                deleted = epic.getSubtasks().remove(id);
            }
        }
        return deleted;
    }

    @Override
    public List<Subtask> getTasksByEpicId (int id) {
        List<Subtask> result = new ArrayList<>();
        if (epics.containsKey(id)) {
            Collection<Subtask> subtasksByEpicId = epics.get(id).getSubtasks().values();
            result.addAll(subtasksByEpicId);
        }
        return result;
    }

    @Override
    public List<Task> getHistory() {
        return history.getHistory();
    }
}
