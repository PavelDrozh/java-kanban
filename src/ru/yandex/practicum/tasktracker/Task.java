package ru.yandex.practicum.tasktracker;

public class Task {

    private final String name;
    private final String description;
    private TaskStatus status;
    private int id;

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = TaskStatus.NEW;
    }

    public Task(String name, String description, int status) {
        this.name = name;
        this.description = description;
        setStatus(status);
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
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
