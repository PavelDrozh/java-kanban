package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.LinkedHashMap;


public class InMemoryTaskManager implements TaskManager {

    private static final int FIRST_INDEX = 0;
    private static final int NEW_TASK = -1;
    private final Map<Integer, Task> tasks;
    private final Map<Integer, Epic> epics;
    private final Map<Integer, Subtask> subtasks;
    private final Map<LocalDateTime, Task> prioritizedTasks;
    private final Map<Integer, Task> notPrioritizedTasks;
    private final HistoryManager history;
    protected int tasksId;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        history = Managers.getDefaultHistory();
        tasksId = FIRST_INDEX;
        prioritizedTasks = new TreeMap<>();
        notPrioritizedTasks = new LinkedHashMap<>();
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
    public Task createTask(Task task) {
        if (task == null || validateTask(task)) return null;
        if (task.getId() == NEW_TASK) {
            tasksId++;
            task.setId(tasksId);
        } else {
            return null;
        }
        if (task.getClass().equals(Subtask.class)) {
            Subtask subtask = (Subtask) task;
            int epicId = subtask.getEpic();
            if (epics.containsKey(epicId)) {
                Epic managersEpic = epics.get(epicId);
                managersEpic.addSubtask(subtask);
                subtasks.put(subtask.getId(), subtask);
                addToPriority(task);
            } else {
                tasksId--;
                return null;
            }
        } else if (task.getClass().equals(Epic.class)) {
            Epic epic = (Epic) task;
            epics.put(epic.getId(), epic);
        } else {
            tasks.put(task.getId(), task);
            addToPriority(task);
        }
        return task;
    }

    private void addToPriority(Task task) {
        if (task.getStartTime() == null) {
            notPrioritizedTasks.put(task.getId(), task);
        } else {
            prioritizedTasks.put(task.getStartTime(), task);
        }
    }

    private boolean validateTask(Task task) {
        boolean result = false;
        if (task.getStartTime() != null) {
            for (Task value : prioritizedTasks.values()) {
                if (task.getStartTime().isAfter(value.getStartTime()) &&
                    task.getStartTime().isBefore(value.getEndTime()) ||
                            task.getEndTime().isAfter(value.getStartTime()) &&
                            task.getEndTime().isBefore(value.getEndTime())) {
                    result = true;
                    break;
                }
            }
        }
        return  result;
    }

    @Override
    public boolean updateTask(Task task) {
        if (task == null || validateTask(task)) return false;
        boolean result = false;
        if (tasks.containsKey(task.getId()) && task.getClass().equals(Task.class)) {
            updatePriority(task);
            tasks.put(task.getId(), task);
            result = true;
        } else if (epics.containsKey(task.getId()) && task.getClass().equals(Epic.class)) {
            result = updateEpic((Epic)task);
        } else if (subtasks.containsKey(task.getId()) && task.getClass().equals(Subtask.class)) {
            result = updateSubtask((Subtask) task);
        }
        return result;
    }

    private void updatePriority(Task task) {
        if (notPrioritizedTasks.containsKey(task.getId()) && task.getStartTime() == null) {
            notPrioritizedTasks.put(task.getId(), task);
        } else if (notPrioritizedTasks.containsKey(task.getId())) {
            notPrioritizedTasks.remove(task.getId());
            prioritizedTasks.put(task.getStartTime(), task);
        } else {
            LocalDateTime oldTime;
            if (task.getClass().equals(Task.class)) {
                oldTime = tasks.get(task.getId()).getStartTime();
            } else {
                oldTime = subtasks.get(task.getId()).getStartTime();
            }
            prioritizedTasks.remove(oldTime);
            prioritizedTasks.put(task.getStartTime(), task);
        }
    }

    private boolean updateEpic(Epic epic) {
        boolean chekSubtask = true;
        for (Subtask sub: epic.getSubtasks().values()) {
            if (!subtasks.containsKey(sub.getId())) {
                chekSubtask = false;
                break;
            }
        }
        if (chekSubtask) {
            for (Subtask sub: epic.getSubtasks().values()) {
                updateSubtask(sub);
            }
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }

    private boolean updateSubtask(Subtask task) {
        boolean result = false;
        int epicId = task.getEpic();
        if (epics.containsKey(epicId)) {
            Epic managersEpic = epics.get(epicId);
            if (managersEpic.getSubtasks().containsKey(task.getId())) {
                managersEpic.addSubtask(task);
                updatePriority(task);
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
        if (result != null) history.add(result);
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
        if (deleted != null) history.remove(id);
        return deleted;
    }

    private Task deleteEpic(int id) {
        Task deleted;
        Epic epicToDelete = epics.get(id);
        Set<Integer> subtasksId = epicToDelete.getSubtasks().keySet();

        for (Integer subtaskId : subtasksId) {
            subtasks.remove(subtaskId);
            history.remove(subtaskId);
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

    @Override
    public List<Task> getPrioritizedTasks() {
        ArrayList<Task> result = new ArrayList<>(prioritizedTasks.values());
        result.addAll(notPrioritizedTasks.values());
        return result;
    }

    public HistoryManager historyManager() {
        return history;
    }
}
