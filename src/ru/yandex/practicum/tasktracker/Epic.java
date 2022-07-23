package ru.yandex.practicum.tasktracker;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new HashMap<>();
    }

    public Map<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void deleteSubtasks() {
        subtasks.clear();
    }

    @Override
    public TaskStatus getStatus() {
        TaskStatus resultStatus;

        if (subtasks.isEmpty()) {
            resultStatus = TaskStatus.NEW;
        } else {
            resultStatus = calculateStatus();
        }
        return resultStatus;
    }

    private TaskStatus calculateStatus() {
        TaskStatus resultStatus;
        int newSubtasks = 0;
        int inProgressSubtasks = 0;
        int doneSubtasks = 0;

        for (Subtask subtask : subtasks.values()) {
            if (subtask.getStatus().equals(TaskStatus.NEW)) {
                newSubtasks++;
            } else if (subtask.getStatus().equals(TaskStatus.IN_PROGRESS)) {
                inProgressSubtasks++;
            } else {
                doneSubtasks++;
            }
        }
        if (newSubtasks != 0 && inProgressSubtasks == 0 && doneSubtasks == 0) {
            resultStatus = TaskStatus.NEW;
        } else if (newSubtasks == 0 && inProgressSubtasks == 0 && doneSubtasks != 0) {
            resultStatus = TaskStatus.DONE;
        } else {
            resultStatus = TaskStatus.IN_PROGRESS;
        }
        return resultStatus;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasksId=" + subtasks.keySet() +
                '}';
    }
}
