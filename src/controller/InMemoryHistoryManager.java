package controller;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private ArrayList<Task> tasksHistory = new ArrayList<>(10);

    @Override
    public void add(Task task) {
        if (tasksHistory.size() == 10) {
            tasksHistory.removeFirst();
        }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return tasksHistory;
    }
}
