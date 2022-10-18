package ru.yandex.practicum.tasktracker.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.tasktracker.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    private Epic epic;


    @BeforeEach
    void createEpic() {
        epic = new Epic(1,"epic", "description");
    }

    @Test
    void mustReturnNewWhenEmpty() {
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.NEW;
        assertEquals(real,expected);
    }

    private void addNewSubtaskAndCheckWithExpectedStatus(TaskStatus firstTaskStatus, TaskStatus secondTaskStatus,
                                                         TaskStatus thirdTaskStatus, TaskStatus expected) {
        epic.addSubtask(new Subtask(2, "sub1", "discr1", firstTaskStatus.getId(), epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(3, "sub2", "discr2", secondTaskStatus.getId(), epic.getId(),
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(4, "sub3", "discr3", thirdTaskStatus.getId(), epic.getId(),
                LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(15)));
        TaskStatus real = epic.getStatus();
        assertEquals(expected,real);
    }

    @Test
    void mustReturnNewWhenAllSubsNew() {
        addNewSubtaskAndCheckWithExpectedStatus(TaskStatus.NEW, TaskStatus.NEW, TaskStatus.NEW, TaskStatus.NEW);
    }

    @Test
    void mustReturnInProgressWhenAllSubsNewOrDone() {
        addNewSubtaskAndCheckWithExpectedStatus(TaskStatus.DONE, TaskStatus.DONE, TaskStatus.NEW,
                TaskStatus.IN_PROGRESS);
    }

    @Test
    void mustReturnInProgressWhenAllSubsInProgress() {
        addNewSubtaskAndCheckWithExpectedStatus(TaskStatus.IN_PROGRESS, TaskStatus.IN_PROGRESS, TaskStatus.IN_PROGRESS,
                TaskStatus.IN_PROGRESS);
    }

    @Test
    void mustReturnDoneWhenAllSubsDone() {
        addNewSubtaskAndCheckWithExpectedStatus(TaskStatus.DONE, TaskStatus.DONE, TaskStatus.DONE, TaskStatus.DONE);
    }
}