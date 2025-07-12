package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private final List<Subtask> subtasks = new ArrayList<>();
    private LocalDateTime endTime;

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

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public List<Integer> getSubtasksIds() {
        List<Integer> subtasksIds = new ArrayList<>();
        subtasks.forEach(subtask -> subtasksIds.add(subtask.getId()));
        return subtasksIds;
    }

    @Override
    public String toString() {
        return String.format("%d,EPIC,%s,%s,%s,%s,%s", id, name, status, description,
                duration != null ? duration.toMinutes() : "null",
                startTime != null ? startTime : "null");
    }
}
