package ru.yandex.practicum.tasktracker.data;

import ru.yandex.practicum.tasktracker.enums.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, int epicId, LocalDateTime startTime, Duration duration) {
        super(name, description, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int status, int epicId,
                   LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpic() {
        return epicId;
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s,%s,%s,%d", super.getId(), TaskTypes.SUBTASK, super.getName(),
                super.getStatus(), super.getDescription(), super.getStartTime(), super.getEndTime(), getEpic());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask sub = (Subtask) o;
        return Objects.equals(this.getEpic(), sub.getEpic());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.epicId);
    }
}
