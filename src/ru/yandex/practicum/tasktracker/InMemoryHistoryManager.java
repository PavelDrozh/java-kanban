package ru.yandex.practicum.tasktracker;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10;
    private static final int FIRST_INDEX = 0;
    private final List<Task> history;

    public InMemoryHistoryManager() {
        history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            history.add(task);
            if (history.size() > MAX_HISTORY_SIZE) {
                history.remove(FIRST_INDEX);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
