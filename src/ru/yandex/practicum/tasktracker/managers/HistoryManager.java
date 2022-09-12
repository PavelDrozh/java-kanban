package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.data.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remove(int id);

    List<Task> getHistory();
}
