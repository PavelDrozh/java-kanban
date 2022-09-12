package ru.yandex.practicum.tasktracker.managers;

import ru.yandex.practicum.tasktracker.data.Node;
import ru.yandex.practicum.tasktracker.data.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private static final int NO_ELEMENT = -1;
    private final Map<Integer, Node> history;
    private int head;
    private int tail;

    public InMemoryHistoryManager() {
        history = new HashMap<>();
        head = NO_ELEMENT;
        tail = NO_ELEMENT;
    }

    @Override
    public void add(Task task) {
        if (task != null) {
            if (history.isEmpty()) {
                history.put(task.getId(), new Node(task));
                head = task.getId();
                tail = task.getId();
            } else {
                linkLast(task);
            }
        }
    }

    private void linkLast(Task task) {
        Node tailNode = history.get(tail);
        if (history.containsKey(task.getId())) {
            Node toLink = history.get(task.getId());
            Node prev = history.get(toLink.getPrevTaskId());
            Node next = history.get(toLink.getNextTaskId());

            if (task.getId() != tail && task.getId() != head) {
                prev.setNextTaskId(toLink.getNextTaskId());
                next.setPrevTaskId(toLink.getPrevTaskId());
                updateTail(tailNode, toLink);
            } else if (task.getId() == head && task.getId() != tail) {
                head = toLink.getNextTaskId();
                next.setPrevTaskId(NO_ELEMENT);
                updateTail(tailNode, toLink);
            }
        } else {
            history.put(task.getId(), new Node(task, tail));
            tailNode.setNextTaskId(task.getId());
            tail = task.getId();
        }
    }

    private void updateTail(Node tailNode, Node toLink) {
        toLink.setNextTaskId(NO_ELEMENT);
        toLink.setPrevTaskId(tail);
        tail = toLink.getTask().getId();
        tailNode.setNextTaskId(tail);
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node toDelete = history.get(id);
            removeNode(toDelete);
            history.remove(id);
        }
    }

    private void removeNode(Node toDelete) {
        int idToDelete = toDelete.getTask().getId();
        Node prev = history.get(toDelete.getPrevTaskId());
        Node next = history.get(toDelete.getNextTaskId());
        if (idToDelete != tail && idToDelete != head) {
            prev.setNextTaskId(toDelete.getNextTaskId());
            next.setPrevTaskId(toDelete.getPrevTaskId());
        } else if (idToDelete == tail && idToDelete != head) {
            prev.setNextTaskId(NO_ELEMENT);
            tail = toDelete.getPrevTaskId();
        } else if (idToDelete != tail) {
            next.setPrevTaskId(NO_ELEMENT);
            head = toDelete.getNextTaskId();
        }
    }

    @Override
    public List<Task> getHistory() {
        List<Task> historyList = new ArrayList<>();
        if (!history.isEmpty()) {
            Node current = history.get(head);
            historyList.add(current.getTask());
            while (current.hasNext()) {
                int nextTaskId = current.getNextTaskId();
                current = history.get(nextTaskId);
                historyList.add(current.getTask());
            }
        }
        return historyList;
    }
}
