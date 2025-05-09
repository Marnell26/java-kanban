package controller;

import model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static final int MAX_SIZE_OF_HISTORY_LIST = 10;
    private final List<Task> tasksHistory = new LinkedList<>();

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == MAX_SIZE_OF_HISTORY_LIST) {
            tasksHistory.removeFirst();
        }
        tasksHistory.addLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(tasksHistory);
    }

}
