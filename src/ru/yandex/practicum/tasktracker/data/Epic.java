package ru.yandex.practicum.tasktracker.data;

import ru.yandex.practicum.tasktracker.enums.TaskStatus;
import ru.yandex.practicum.tasktracker.enums.TaskTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Epic extends Task {

    private final Map<Integer, Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.subtasks = new HashMap<>();
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasks = new HashMap<>();
    }

    public Epic(int id, String name, String description, Map<Integer, Subtask> subtasks) {
        super(id, name, description);
        this.subtasks = subtasks;
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
    public LocalDateTime getStartTime() {
        LocalDateTime dateTime;
        if (subtasks.isEmpty()) {
            dateTime = LocalDateTime.MIN;
        } else {
            dateTime = LocalDateTime.MAX;
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getStartTime().isBefore(dateTime)) {
                    dateTime = subtask.getStartTime();
                }
            }

        }
        return dateTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        LocalDateTime dateTime;
        if (subtasks.isEmpty()) {
            dateTime = LocalDateTime.MIN;
        } else {
            dateTime = LocalDateTime.MIN;
            for (Subtask subtask : subtasks.values()) {
                if (subtask.getEndTime().isAfter(dateTime)) {
                    dateTime = subtask.getEndTime();
                }
            }

        }
        return dateTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,", super.getId(), TaskTypes.EPIC, super.getName(),
                this.getStatus(), super.getDescription(), this.getStartTime(), this.getEndTime());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Epic epic = (Epic) o;
        return Objects.equals(this.getId(), epic.getId()) && Objects.equals(this.getName(), epic.getName())
                && Objects.equals(this.getDescription(), epic.getDescription()) && this.getStatus() == epic.getStatus()
                && Objects.equals(this.getStartTime(), epic.getStartTime())
                && Objects.equals(this.getEndTime(), epic.getEndTime())
                && Objects.equals(this.getSubtasks(), epic.getSubtasks());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.subtasks);
    }
}
