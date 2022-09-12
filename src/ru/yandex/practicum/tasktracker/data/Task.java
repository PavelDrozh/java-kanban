package ru.yandex.practicum.tasktracker.data;

import ru.yandex.practicum.tasktracker.enums.TaskStatus;
import ru.yandex.practicum.tasktracker.enums.TaskTypes;

public class Task {

    private static final int NEW_TASK = - 1;
    private final String name;
    private final String description;
    private TaskStatus status;
    private int id;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
        this.id = NEW_TASK;
    }

    public Task(String name, String description, int status) {
        this.name = name;
        this.description = description;
        setStatus(status);
        this.id = NEW_TASK;
    }
    public Task(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(int id, String name, String description, int status) {
        this.id = id;
        this.name = name;
        this.description = description;
        setStatus(status);
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

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,", id, TaskTypes.TASK, name, status, description);
    }
}
