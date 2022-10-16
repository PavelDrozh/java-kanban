package ru.yandex.practicum.tasktracker.data;

import ru.yandex.practicum.tasktracker.enums.TaskStatus;
import ru.yandex.practicum.tasktracker.enums.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private static final int NEW_TASK = 0;

    private int id;
    private final String name;
    private final String description;
    private TaskStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Task(String name, String description) {
        this.id = NEW_TASK;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = null;
        this.endTime = null;
    }

    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = null;
        this.endTime = null;
    }

    public Task(int id, String name, String description, int status) {
        this.id = id;
        this.name = name;
        this.description = description;
        setStatus(status);
        this.startTime = null;
        this.endTime = null;
    }

    public Task(String name, String description, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.id = NEW_TASK;
        this.startTime = startTime;
        this.endTime = startTime.plus(duration);
    }

    public Task(int id, String name, String description, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.startTime = startTime;
        this.endTime = startTime.plus(duration);
    }

    public Task(int id, String name, String description, int status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        setStatus(status);
        this.startTime = startTime;
        this.endTime = startTime.plus(duration);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    private void setStatus(int status) {
        for (TaskStatus stat: TaskStatus.values()) {
            if (stat.getId() == status) {
                this.status = stat;
            }
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,",
                id, TaskTypes.TASK, name, status, description, startTime, endTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(this.getId(), task.getId()) && Objects.equals(this.getName(), task.getName())
                && Objects.equals(this.getDescription(), task.getDescription()) && this.getStatus() == task.getStatus()
                && Objects.equals(this.getStartTime(), task.getStartTime())
                && Objects.equals(this.getEndTime(), task.getEndTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId(), this.getName(),
                this.getDescription(), this.getStatus());
    }
}
