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
        addNewSubtask(TaskStatus.NEW.getId(), TaskStatus.NEW.getId(), TaskStatus.NEW.getId());
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.NEW;
        assertEquals(expected,real);
    }

    @Test
    void mustReturnInProgressWhenAllSubsNewOrDone() {
        addNewSubtask(TaskStatus.DONE.getId(), TaskStatus.DONE.getId(), TaskStatus.NEW.getId());
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        assertEquals(expected,real);
    }

    @Test
    void mustReturnInProgressWhenAllSubsInProgress() {
        addNewSubtask(TaskStatus.IN_PROGRESS.getId(), TaskStatus.IN_PROGRESS.getId(), TaskStatus.IN_PROGRESS.getId());
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.IN_PROGRESS;
        assertEquals(expected,real);
    }

    @Test
    void mustReturnDoneWhenAllSubsDone() {
        addNewSubtask(TaskStatus.DONE.getId(), TaskStatus.DONE.getId(), TaskStatus.DONE.getId());
        TaskStatus real = epic.getStatus();
        TaskStatus expected = TaskStatus.DONE;
        assertEquals(expected,real);
    }

    private void addNewSubtask(int firstTaskStatus, int secondTaskStatus, int thirdTaskStatus) {
        epic.addSubtask(new Subtask(2, "sub1", "discr1", firstTaskStatus, epic.getId(),
                LocalDateTime.of(2022, 10, 1, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(3, "sub2", "discr2", secondTaskStatus, epic.getId(),
                LocalDateTime.of(2022, 10, 2, 15, 52), Duration.ofMinutes(15)));
        epic.addSubtask(new Subtask(4, "sub3", "discr3", thirdTaskStatus, epic.getId(),
                LocalDateTime.of(2022, 10, 3, 15, 52), Duration.ofMinutes(15)));
    }
}