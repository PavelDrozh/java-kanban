package ru.yandex.practicum.tasktracker.enums;

public enum CSVColumns {

    ID(0),
    TYPE(1),
    NAME(2),
    STATUS(3),
    DESCRIPTION(4),
    START_TIME(5),
    END_TIME(6),
    EPIC(7);

    private final int id;

    CSVColumns(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
