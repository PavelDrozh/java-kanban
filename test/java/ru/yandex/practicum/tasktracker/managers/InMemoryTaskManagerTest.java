package ru.yandex.practicum.tasktracker.managers;

import org.junit.jupiter.api.BeforeEach;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void createManager() {
        super.manager = (InMemoryTaskManager) Managers.getDefault();
    }

}
