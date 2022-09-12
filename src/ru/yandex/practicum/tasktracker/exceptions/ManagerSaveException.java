package ru.yandex.practicum.tasktracker.exceptions;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException() {
        super();
    }

    public ManagerSaveException(final String message) {
        super(message);
    }
}
