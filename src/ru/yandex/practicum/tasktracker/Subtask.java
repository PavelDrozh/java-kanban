package ru.yandex.practicum.tasktracker;

public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public Subtask(String name, String description, int status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    public int getEpic() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + super.getId() +
                ", name='" + super.getName() + '\'' +
                ", description='" + super.getDescription() + '\'' +
                ", status='" + super.getStatus() + '\'' +
                ", epicId=" + epicId +
                '}';
    }
}
