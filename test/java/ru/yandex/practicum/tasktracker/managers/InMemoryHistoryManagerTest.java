package ru.yandex.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager history;
    private final Task task1 = new Task(1,"Task1", "Description1",0,
            LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
    private final Task epic1 = new Epic(2,"Epic1", "Description3");
    private final Task subtask1 = new Subtask(3, "SubTask1", "Description4", -1,2,
            LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(15));
    private final Task epic2 = new Epic(4,"Epic2", "Description5");
    private final Task subtask2 = new Subtask(5, "SubTask1", "Description4", 1,4,
            LocalDateTime.of(2022, 10, 4, 15, 52), Duration.ofMinutes(15));
    private final Task task2 = new Task(6,"Task2", "Description2",
            LocalDateTime.of(2022, 10, 5, 15, 52), Duration.ofMinutes(15));

    @BeforeEach
    void createManager() {
        history = Managers.getDefaultHistory();
    }


    @Test
    void mustHaveEmptyListWhenAddedTaskIsNull() {
        history.add(null);
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void mustHaveTasksListWhenAddedTaskIsNotNull() {
        history.add(task2);
        assertFalse(history.getHistory().isEmpty());
        List<Task> expected = List.of(task2);
        assertIterableEquals(expected, history.getHistory());
    }

    @Test
    void mustHaveOneElemListWhenAddedOneTaskManyTimes() {
        history.add(task1);
        history.add(task1);
        assertFalse(history.getHistory().isEmpty());
        List<Task> expected = List.of(task1);
        assertIterableEquals(expected, history.getHistory());
    }

    @Test
    void mustHaveSixElemListWhenAddedManyTaskManyTimes() {
        fillHistory();
        history.add(task1);
        history.add(subtask2);
        assertFalse(history.getHistory().isEmpty());
        assertEquals(6, history.getHistory().size());
        List<Task> expected = List.of(epic1, subtask1, epic2, task2, task1, subtask2);
        assertIterableEquals(expected, history.getHistory());
    }

    private void fillHistory() {
        history.add(task1);
        history.add(epic1);
        history.add(subtask1);
        history.add(epic2);
        history.add(subtask2);
        history.add(task2);
    }

    @Test
    void mustHaveFiveElemListWhenDeletedCorrectID() {
        fillHistory();
        history.remove(5);
        assertEquals(5, history.getHistory().size());
        List<Task> expected = List.of(task1, epic1, subtask1, epic2, task2);
        assertIterableEquals(expected, history.getHistory());
    }

    @Test
    void mustHaveSixElemListWhenDeletedIncorrectID() {
        fillHistory();
        history.remove(-1);
        assertEquals(6, history.getHistory().size());
        List<Task> expected = List.of(task1, epic1, subtask1, epic2, subtask2, task2);
        assertIterableEquals(expected, history.getHistory());
    }

    @Test
    void mustHaveEmptyListWhenDeleteFromEmptyHistory() {
        history.remove(1);
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void mustHaveEmptyListWhenDeleteLastTask() {
        history.add(task1);
        history.remove(1);
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void mustHaveFiveElemListWhenDeletedCorrectIdIsHead() {
        fillHistory();
        history.remove(1);
        assertEquals(5, history.getHistory().size());
        List<Task> expected = List.of(epic1, subtask1, epic2, subtask2, task2);
        assertIterableEquals(expected, history.getHistory());
    }

    @Test
    void mustHaveFiveElemListWhenDeletedCorrectIdIsTail() {
        fillHistory();
        history.remove(6);
        assertEquals(5, history.getHistory().size());
        List<Task> expected = List.of(task1, epic1, subtask1, epic2, subtask2);
        assertIterableEquals(expected, history.getHistory());
    }
}