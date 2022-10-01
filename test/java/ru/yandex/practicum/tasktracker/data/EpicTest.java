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

    @Test
    void mustReturnNewWhenAllSubsNew() {
        epic.addSubtask(new Subtask("sub1", "discr1", epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask("sub2", "discr2", epic.getId(),
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask("sub3", "discr3", epic.getId(),
                LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(15)));
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.NEW;
        assertEquals(real,expected);
    }

    @Test
    void mustReturnInProgressWhenAllSubsNewOrDone() {
        epic.addSubtask(new Subtask(2,"sub1", "discr1",1, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(3,"sub2", "discr2", 1, epic.getId(),
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(4,"sub3", "discr3",-1, epic.getId(),
                LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(15)));
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        assertEquals(real,expected);
    }

    @Test
    void mustReturnInProgressWhenAllSubsInProgress() {
        epic.addSubtask(new Subtask(2,"sub1", "discr1",0, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(3,"sub2", "discr2", 0, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(4,"sub3", "discr3",0, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        assertEquals(real,expected);
    }

    @Test
    void mustReturnDoneWhenAllSubsDone() {
        epic.addSubtask(new Subtask(2,"sub1", "discr1",1, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(3,"sub2", "discr2", 1, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(4,"sub3", "discr3",1, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.DONE;
        assertEquals(real,expected);
    }
}