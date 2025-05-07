package controller;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task> tasksHistory = new ArrayList<>();
    private static final int MAX_SIZE_OF_HISTORY_LIST = 10;

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == MAX_SIZE_OF_HISTORY_LIST) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(tasksHistory);
    }

    public static int getMaxSizeOfHistoryList() {
        return MAX_SIZE_OF_HISTORY_LIST;
    }
}
