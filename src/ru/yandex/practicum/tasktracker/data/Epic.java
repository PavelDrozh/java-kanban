package ru.yandex.practicum.tasktracker.data;

import ru.yandex.practicum.tasktracker.enums.TaskStatus;
import ru.yandex.practicum.tasktracker.enums.TaskTypes;

import java.util.HashMap;
import java.util.Map;

public class Epic extends Task {

    private final HashMap<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new HashMap<>();
    }

    public Epic(int id, String name, String description, int status) {
        super(id, name, description, status);
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
        }
        return resultStatus;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", super.getId(), TaskTypes.EPIC, super.getName(),
                super.getStatus(), super.getDescription());
    }
}
