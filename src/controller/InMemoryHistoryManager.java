package controller;

import model.Task;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> tasksHistory = new HashMap<>();
    private Node historyHead;
    private Node historyTail;

    class Node {
        Task task;
        Node prev;
        Node next;

        Node(Task task) {
            this.task = task;
        }

    }

    @Override
    public void add(Task task) {
        if (tasksHistory.containsKey(task.getId())) {
            removeNode(tasksHistory.get(task.getId()));
        }
        Node newNode = new Node(task);
        linkLast(newNode);
        tasksHistory.put(task.getId(), newNode);
    }

    private void linkLast(Node node) {
        if (historyHead == null) {
            historyHead = node;
        } else {
            historyTail.next = node;
            node.prev = historyTail;
        }
        historyTail = node;
    }

    public void getTasks() {

    }

    @Override
    public void remove(int id) {
        if (tasksHistory.containsKey(id)) {
            removeNode(tasksHistory.get(id));
            tasksHistory.remove(id);
        }
    }

    private void removeNode(Node node) {
        if (node == historyHead) {
            historyHead = node.next;
        }
        if (node == historyTail) {
            historyTail = node.prev;
            if (historyTail != null) {
                historyTail.next = null;
            }
        }
        Node prev = node.prev;
        Node next = node.next;
        if (prev != null && next != null) {
            prev.next = next;
            next.prev = prev;
        }

    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>();
    }

}
