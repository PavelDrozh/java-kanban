package ru.yandex.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;
import ru.yandex.practicum.tasktracker.exceptions.ManagerSaveException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @BeforeEach
    void createManager() {
        super.manager = Managers.getDefaultFileBacked();
    }

    @Test
    void getFileBackedFromEmptyFile() {
        File file = new File("test/resources/emptyfile.csv");
        FileBackedTaskManager realManger = Managers.loadFromFile(file);
        assertTrue(realManger.getTasksList().isEmpty());
        assertTrue(realManger.getEpicsList().isEmpty());
        assertTrue(realManger.getSubtasksList().isEmpty());
        assertTrue(realManger.getHistory().isEmpty());
    }

    @Test
    void getFileBackedFromNormalFile() {
        File file = new File("test/resources/taskstracktest.csv");
        FileBackedTaskManager expectedManger = Managers.loadFromFile(file);
        List<Task> expectedTasks = expectedManger.getTasksList();
        List<Epic> expectedEpic = expectedManger.getEpicsList();
        List<Subtask> expectedSubtasks = expectedManger.getSubtasksList();
        List<Integer> expectedHistory = new ArrayList<>();
        expectedManger.getHistory().stream().mapToInt(Task::getId).forEach(expectedHistory::add);
        assertFalse(expectedTasks.isEmpty());
        assertFalse(expectedEpic.isEmpty());
        assertFalse(expectedSubtasks.isEmpty());
        assertFalse(expectedHistory.isEmpty());
        File realFile = new File("resources/taskstrack.csv");
        FileBackedTaskManager realManger = Managers.loadFromFile(realFile);
        List<Task> realTasks = realManger.getTasksList();
        List<Epic> realEpic = realManger.getEpicsList();
        List<Subtask> realSubtasks = realManger.getSubtasksList();
        List<Integer> realHistory = new ArrayList<>();
        realManger.getHistory().stream().mapToInt(Task::getId).forEach(realHistory::add);
        assertIterableEquals(expectedTasks, realTasks);
        assertIterableEquals(expectedEpic, realEpic);
        assertIterableEquals(expectedSubtasks, realSubtasks);
        assertIterableEquals(expectedHistory, realHistory);
    }

    @Test
    void getFileBackedFromNotExistingFile() {
        ManagerSaveException ex = getManagerSaveException("test/resources/incorrect.csv");
        assertEquals("Ошибка чтения задач из файла", ex.getMessage());
    }

    @Test
    void getFileBackedFromIncorrectTasks() {
        ManagerSaveException ex = getManagerSaveException("test/resources/incorrectTasks.csv");
        assertEquals("Ошибка чтения задач из файла", ex.getMessage());
    }

    @Test
    void getFileBackedFromIncorrectHistory() {
        ManagerSaveException ex = getManagerSaveException("test/resources/incorrectHistory.csv");
        assertEquals("Ошибка чтения истории из файла", ex.getMessage());
    }

    private ManagerSaveException getManagerSaveException(String s) {
        File file = new File(s);
        return assertThrows(ManagerSaveException.class,
                () -> Managers.loadFromFile(file));
    }
}
