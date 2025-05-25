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
        private final Task task;
        private Node prev;
        private Node next;

        Node(Task task) {
            this.task = task;
        }

        public Node getPrev() {
            return prev;
        }

        public Task getItem() {
            return task;
        }

        public Node getNext() {
            return next;
        }

        public void setPrev(Node prev) {
            this.prev = prev;
        }

        public void setNext(Node next) {
            this.next = next;
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
            historyTail.setNext(node);
            node.setPrev(historyTail);
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
            historyHead = node.getNext();
        }
        if (node == historyTail) {
            historyTail = node.getPrev();
            if (historyTail != null) {
                historyTail.setNext(null);
            }
        }
        Node prev = node.getPrev();
        Node next = node.getNext();
        if (prev != null && next != null) {
            prev.setNext(next);
            next.setPrev(prev);
        }

    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>();
    }

}
