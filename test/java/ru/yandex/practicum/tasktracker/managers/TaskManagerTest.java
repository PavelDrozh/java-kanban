package ru.yandex.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.tasktracker.data.Epic;
import ru.yandex.practicum.tasktracker.data.Subtask;
import ru.yandex.practicum.tasktracker.data.Task;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;
    protected final Task task1 = new Task("Task1", "Description1",
            LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
    protected final Task epic1 = new Epic("Epic1", "Description3");
    protected final Task subtask1 = new Subtask("SubTask1", "Description4",2,
            LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
    protected final Task epic2 = new Epic("Epic2", "Description5");
    protected final Task subtask2 = new Subtask("SubTask1", "Description4",4,
            LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(30));
    protected final Task task2 = new Task("Task2", "Description2",
            LocalDateTime.of(2022, 10, 4, 15, 52), Duration.ofMinutes(45));

    protected final Task incorrectTask1 = new Task(-20, "Task1", "Description1",
            LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
    protected final Task incorrectTask2 = new Task(12, "Task2", "Description2",
            LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
    protected final Task incorrectEpic1 = new Epic(73,"Epic1", "Description3");
    protected final Task incorrectSubtask1 = new Subtask("SubTask1", "Description4",5,
            LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
    protected final Task incorrectEpic2 = new Epic(-5,"Epic2", "Description5");
    protected final Task incorrectSubtask2 = new Subtask("SubTask1", "Description4",8,
            LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));

    @BeforeEach
    abstract void createManager() throws IOException;

    @Test
    void mustReturnNellWhenCreateTaskNull() {
        Task real = manager.createTask(null);
        assertNull(real);
    }

    @Test
    void mustReturnTaskWhenCreateTaskCorrect() {
        Task task = manager.createTask(task1);
        Task expectedTask = new Task(1,"Task1", "Description1",
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
        assertEquals(expectedTask,task);
        Task epic = manager.createTask(epic1);
        Task expectedEpic = new Epic(2, "Epic1", "Description3");
        assertEquals(expectedEpic,epic);
        Task subtask = manager.createTask(subtask1);
        Task expectedSub = new Subtask(3,"SubTask1", "Description4",-1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        assertEquals(expectedSub,subtask);
    }

    protected void normalFilling() {
        manager.createTask(task1);
        manager.createTask(epic1);
        manager.createTask(subtask1);
        manager.createTask(epic2);
        manager.createTask(subtask2);
        manager.createTask(task2);
    }

    protected void incorrectFilling() {
        manager.createTask(incorrectTask1);
        manager.createTask(incorrectTask2);
        manager.createTask(incorrectEpic1);
        manager.createTask(incorrectSubtask1);
        manager.createTask(incorrectEpic2);
        manager.createTask(incorrectSubtask2);
    }

    @Test
    void mustReturnEmptyTaskListWhenNoTasks() {
        List<Task> real = manager.getTasksList();
        assertTrue(real.isEmpty());
    }

    @Test
    void mustReturnTaskListWhenSomeTasksAdded() {
        List<Task> expected = List.of(task1, task2);
        normalFilling();
        List<Task> real = manager.getTasksList();
        assertFalse(real.isEmpty());
        assertIterableEquals(expected, real);
    }

    @Test
    void mustReturnEmptyTaskListWhenIncorrectTasks() {
        incorrectFilling();
        List<Task> real = manager.getTasksList();
        assertTrue(real.isEmpty());
    }


    @Test
    void mustReturnEmptyEpicListWhenNoTasks() {
        List<Epic> real = manager.getEpicsList();
        assertTrue(real.isEmpty());
    }

    @Test
    void mustReturnEpicListWhenSomeTasksAdded() {
        List<Task> expected = List.of(epic1, epic2);
        normalFilling();
        List<Epic> real = manager.getEpicsList();
        assertFalse(real.isEmpty());
        assertIterableEquals(expected, real);
    }

    @Test
    void mustReturnEmptyEpicListWhenIncorrectTasks() {
        incorrectFilling();
        List<Epic> real = manager.getEpicsList();
        assertTrue(real.isEmpty());
    }

    @Test
    void mustReturnEmptySubListWhenNoTasks() {
        List<Subtask> real = manager.getSubtasksList();
        assertTrue(real.isEmpty());
    }

    @Test
    void mustReturnSubListWhenSomeTasksAdded() {
        List<Task> expected = List.of(subtask1, subtask2);
        normalFilling();
        List<Subtask> real = manager.getSubtasksList();
        assertFalse(real.isEmpty());
        assertIterableEquals(expected, real);
    }

    @Test
    void mustReturnEmptySubListWhenIncorrectTasks() {
        incorrectFilling();
        List<Subtask> real = manager.getSubtasksList();
        assertTrue(real.isEmpty());
    }

    @Test
    void mustReturnFalseWhenUpdateNull() {
        normalFilling();
        boolean result = manager.updateTask(null);
        assertFalse(result);
    }

    @Test
    void mustReturnTrueWhenUpdateTask() {
        normalFilling();
        Task newTask = new Task(1,"Task1", "Description1", 1,
                LocalDateTime.of(2022, 10, 5, 15, 52), Duration.ofMinutes(25));
        boolean taskUpdate = manager.updateTask(newTask);
        assertTrue(taskUpdate);
        Map<Integer, Subtask> subtasksMap = new HashMap<>();
        Subtask subtask = new Subtask(3,"SubTask1", "Description4", 1,2,
                LocalDateTime.of(2022, 10, 10, 15, 52), Duration.ofMinutes(25));
        subtasksMap.put(subtask.getId(), subtask);
        Task newEpic = new Epic(2,"Task1", "Description1", subtasksMap);
        boolean epicUpdate = manager.updateTask(newEpic);
        assertTrue(epicUpdate);
        Subtask subtaskForUpdate = new Subtask(5,"SubTask1", "Description4", 1,4,
                LocalDateTime.of(2022, 10, 12, 15, 52), Duration.ofMinutes(25));
        boolean subtaskUpdate = manager.updateTask(subtaskForUpdate);
        assertTrue(subtaskUpdate);
    }

    @Test
    void mustReturnFalseWhenUpdateIncorrectTask() {
        normalFilling();
        Task newTask = new Task(-10,"INCORRECT Task", "Description1", 1,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        boolean taskUpdate = manager.updateTask(newTask);
        assertFalse(taskUpdate);
        Map<Integer, Subtask> subtasksMap = new HashMap<>();
        Subtask subtask = new Subtask(8,"Incorrect SubTask", "Description4", 1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        subtasksMap.put(subtask.getId(), subtask);
        Task newEpic = new Epic(2,"Task1", "Description1", subtasksMap);
        boolean epicUpdate = manager.updateTask(newEpic);
        assertFalse(epicUpdate);
        Subtask subtaskForUpdate = new Subtask(5,"SubTask1", "Description4", 1,7,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        boolean subtaskUpdate = manager.updateTask(subtaskForUpdate);
        assertFalse(subtaskUpdate);
    }

    @Test
    void mustDeleteAllTasks() throws IOException, InterruptedException {
        normalFilling();
        manager.deleteAllTasks();
        assertTrue(manager.getTasksList().isEmpty());
        assertTrue(manager.getEpicsList().isEmpty());
        assertTrue(manager.getSubtasksList().isEmpty());
    }

    @Test
    void mustDeleteTasks() {
        normalFilling();
        manager.deleteTasks();
        assertTrue(manager.getTasksList().isEmpty());
        assertFalse(manager.getEpicsList().isEmpty());
        assertFalse(manager.getSubtasksList().isEmpty());
    }

    @Test
    void mustDeleteEpicsAndSubtasks() {
        normalFilling();
        manager.deleteEpics();
        assertTrue(manager.getEpicsList().isEmpty());
        assertTrue(manager.getSubtasksList().isEmpty());
        assertFalse(manager.getTasksList().isEmpty());
    }

    @Test
    void mustDeleteSubtasks() {
        normalFilling();
        manager.deleteSubtasks();
        assertTrue(manager.getSubtasksList().isEmpty());
        assertFalse(manager.getEpicsList().isEmpty());
        assertFalse(manager.getTasksList().isEmpty());
        List<Epic> epics = manager.getEpicsList();
        for (Epic epic : epics) {
            assertTrue(epic.getSubtasks().isEmpty());
        }
    }

    @Test
    void mustReturnNullWhenGetFromEmptyTasks() {
        assertNull(manager.getTaskOrNull(1));
        assertNull(manager.getSubtaskOrNull(1));
        assertNull(manager.getEpicOrNull(1));
    }

    @Test
    void mustReturnTaskWhenGetCorrectTask() {
        normalFilling();
        Task takenTask = manager.getTaskOrNull(1);
        assertNotNull(takenTask);
        Task expectedTask = new Task(1,"Task1", "Description1",
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
        assertEquals(expectedTask,takenTask);

        Subtask takenSubtask = manager.getSubtaskOrNull(3);
        assertNotNull(takenSubtask);
        Subtask expectedSubtask = new Subtask(3,"SubTask1", "Description4", -1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        assertEquals(expectedSubtask,takenSubtask);

        Epic takenEpic = manager.getEpicOrNull(2);
        assertNotNull(takenEpic);
        Map<Integer, Subtask> subtasksMap = new HashMap<>();
        Subtask subtask = new Subtask(3,"SubTask1", "Description4", -1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        subtasksMap.put(subtask.getId(), subtask);
        Epic expectedEpic = new Epic(2,"Epic1", "Description3", subtasksMap);
        assertEquals(expectedEpic,takenEpic);
    }

    @Test
    void mustReturnNullWhenGetIncorrectTask() {
        normalFilling();
        assertNull(manager.getTaskOrNull(-3));
        assertNull(manager.getTaskOrNull(25));
        assertNull(manager.getSubtaskOrNull(-3));
        assertNull(manager.getSubtaskOrNull(25));
        assertNull(manager.getEpicOrNull(-3));
        assertNull(manager.getEpicOrNull(25));
    }

    @Test
    void mustReturnNullWhenDeleteNullTask() {
        assertNull(manager.deleteTaskOrNull(1));
    }

    @Test
    void mustReturnTaskWhenDeleteCorrectTask() {
        normalFilling();
        Task deletedTask = manager.deleteTaskOrNull(1);
        assertNotNull(deletedTask);
        Task expectedTask = new Task(1,"Task1", "Description1",
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15));
        assertEquals(expectedTask,deletedTask);
    }

    @Test
    void mustReturnTaskWhenDeleteCorrectEpic() {
        normalFilling();
        Epic deletedTask = manager.deleteEpicOrNull(2);
        assertNotNull(deletedTask);
        Map<Integer, Subtask> subtasksMap = new HashMap<>();
        Subtask subtask = new Subtask(3,"SubTask1", "Description4", -1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        subtasksMap.put(subtask.getId(), subtask);
        Epic expectedEpic = new Epic(2,"Epic1", "Description3", subtasksMap);
        assertEquals(expectedEpic,deletedTask);
    }

    @Test
    void mustReturnTaskWhenDeleteCorrectSubtask() {
        normalFilling();
        Task deletedTask = manager.deleteSubtaskOrNull(3);
        assertNotNull(deletedTask);
        Subtask expectedSubtask = new Subtask(3, "SubTask1", "Description4",-1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25));
        assertEquals(expectedSubtask,deletedTask);
        int epicId = expectedSubtask.getEpic();
        Epic epic = manager.getEpicOrNull(epicId);
        assertTrue(epic.getSubtasks().isEmpty());
    }
    @Test
    void mustReturnNullWhenDeleteIncorrectTask() {
        normalFilling();
        Task deletedTask = manager.deleteTaskOrNull(-1);
        assertNull(deletedTask);
        Task deletedTask2 = manager.deleteTaskOrNull(11);
        assertNull(deletedTask2);
        Subtask deletedSubtask = manager.deleteSubtaskOrNull(-1);
        assertNull(deletedSubtask);
        Subtask deletedSubtask2 = manager.deleteSubtaskOrNull(-1);
        assertNull(deletedSubtask2);
        Epic deletedEpic = manager.deleteEpicOrNull(-1);
        assertNull(deletedEpic);
        Epic deletedEpic2 = manager.deleteEpicOrNull(11);
        assertNull(deletedEpic2);
    }

    @Test
    void mustReturnEmptyListWhenGetTasksByEpicIdFromEmptyManager() {
        List<Subtask> subs = manager.getTasksByEpicId(1);
        assertTrue(subs.isEmpty());
    }

    @Test
    void mustReturnSubtaskListWhenGetTasksByCorrectEpicId() {
        normalFilling();
        List<Subtask> subs = manager.getTasksByEpicId(2);
        List<Subtask> expected = List.of(new Subtask(3,"SubTask1", "Description4",-1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25)));
        assertFalse(subs.isEmpty());
        assertIterableEquals(expected, subs);
    }

    @Test
    void mustReturnEmptyListWhenGetTasksByIncorrectEpicId() {
        normalFilling();
        List<Subtask> subs = manager.getTasksByEpicId(1);
        assertTrue(subs.isEmpty());
    }

    @Test
    void mustReturnEmptyListWhenGetHistoryFromEmptyManager() {
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void mustReturnTasksListWhenGetHistoryFromNormalManager() {
        normalFilling();
        manager.getTaskOrNull(1);
        manager.getSubtaskOrNull(3);
        List<Task> expectedHistory = List.of(new Task(1,"Task1", "Description1",
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)),
        new Subtask(3,"SubTask1", "Description4",-1,2,
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25)));
        List<Task> history = manager.getHistory();
        assertFalse(history.isEmpty());
        assertIterableEquals(expectedHistory, history);
    }

    @Test
    void mustReturnEmptyListWhenGetIncorrectHistoryFromNormalManager() {
        normalFilling();
        manager.getTaskOrNull(-1);
        manager.getTaskOrNull(93);
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void mustReturnEmptyListWhenGetPriorityFromEmptyManager() {
        List<Task> result = manager.getPrioritizedTasks();
        assertTrue(result.isEmpty());
    }

    @Test
    void mustReturnPriorityListWhenGetPriorityFromNormalManager() {
        List<Task> expected = List.of(new Task(1,"Task1", "Description1",
                        LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)),
                new Subtask(3,"SubTask1", "Description4",-1,2,
                        LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25)),
                new Subtask(5,"SubTask1", "Description4",-1,4,
                        LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(30)),
                new Task(6,"Task2", "Description2",
                        LocalDateTime.of(2022, 10, 4, 15, 52), Duration.ofMinutes(45)));
        normalFilling();
        List<Task> result = manager.getPrioritizedTasks();
        assertFalse(result.isEmpty());
        assertIterableEquals(expected, result);
    }

    @Test
    void mustReturnPriorityListWhenGetPriorityFromNormalManagerWithNoStartTime() {
        List<Task> expected = List.of(new Task(1,"Task1", "Description1",
                        LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)),
                new Subtask(3,"SubTask1", "Description4",-1,2,
                        LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(25)),
                new Subtask(5,"SubTask1", "Description4",-1,4,
                        LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(30)),
                new Task(6,"Task2", "Description2",
                        LocalDateTime.of(2022, 10, 4, 15, 52), Duration.ofMinutes(45)),
                new Task(7,"Task3", "Description7"));
        normalFilling();
        manager.createTask(new Task("Task3", "Description7"));
        List<Task> result = manager.getPrioritizedTasks();
        assertFalse(result.isEmpty());
        assertIterableEquals(expected, result);
    }
}