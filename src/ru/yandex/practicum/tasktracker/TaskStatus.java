package ru.yandex.practicum.tasktracker;

public enum TaskStatus {
    NEW(-1),
    IN_PROGRESS(0),
    DONE(1);

    private final int id;

    TaskStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
