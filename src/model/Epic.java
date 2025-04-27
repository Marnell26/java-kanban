package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();

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

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public ArrayList<Integer> getSubtasksIds() {
        ArrayList<Integer> subtasksIds = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            subtasksIds.add(subtask.getId());
        }
        return subtasksIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksId=" + getSubtasksIds() +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
