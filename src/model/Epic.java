package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
        this.status = Status.NEW;
    }

    public Epic(int id, String name, String description) {
        super(name, description);
        this.id = id;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public TaskType getType() {
        return TaskType.EPIC;
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public List<Integer> getSubtasksIds() {
        List<Integer> subtasksIds = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            subtasksIds.add(subtask.getId());
        }
        return subtasksIds;
    }

    @Override
    public String toString() {
        return String.format("%d,EPIC,%s,%s,%s", id, name, status, description);
    }
}
