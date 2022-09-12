package ru.yandex.practicum.tasktracker.enums;

public enum CSVColumns {

    ID(0),
    TYPE(1),
    NAME(2),
    STATUS(3),
    DESCRIPTION(4),
    EPIC(5);

    private final int id;

    CSVColumns(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
