package ru.yandex.practicum.tasktracker;

public class Node {

    private static final int NO_ELEMENT = -1;

    private final Task task;
    private int prevTaskId;
    private int nextTaskId;

    public Node(Task task) {
        this.task = task;
        this.prevTaskId = NO_ELEMENT;
        this.nextTaskId = NO_ELEMENT;
    }


    public Node(Task task, int prevTaskId) {
        this.task = task;
        this.prevTaskId = prevTaskId;
        this.nextTaskId = NO_ELEMENT;
    }

    public Task getTask() {
        return task;
    }

    public int getPrevTaskId() {
        return prevTaskId;
    }

    public void setPrevTaskId(int prevTaskId) {
        this.prevTaskId = prevTaskId;
    }

    public int getNextTaskId() {
        return nextTaskId;
    }

    public void setNextTaskId(int nextTaskId) {
        this.nextTaskId = nextTaskId;
    }

    public boolean hasNext() {
        return this.nextTaskId != NO_ELEMENT;
    }
}
