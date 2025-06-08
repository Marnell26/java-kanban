package controller;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> tasksHistory = new HashMap<>();
    private Node head;
    private Node tail;

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
        if (head == null) {
            head = node;
        } else {
            tail.next = node;
            node.prev = tail;
        }
        tail = node;
    }

    @Override
    public void remove(int id) {
        if (tasksHistory.containsKey(id)) {
            removeNode(tasksHistory.get(id));
            tasksHistory.remove(id);
        }
    }

    private void removeNode(Node node) {
        if (node == head) {
            head = node.next;
        }
        if (node == tail) {
            tail = node.prev;
            if (tail != null) {
                tail.next = null;
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
        List<Task> history = new ArrayList<>();
        Node current = head;
        while (current != null) {
            history.add(current.task);
            current = current.next;
        }

        return history;
    }

}
