package controller;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;



public class TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int id = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateTaskId() {
        id++;
        return id;
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void clearTasks() {
        tasks.clear();
    }

    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        int id = generateTaskId();
        task.setId(id);
        tasks.put(id, task);
    }

    public void createEpic(Epic epic) {
        id = generateTaskId();
        epic.setId(id);
        epics.put(id, epic);
    }

    public void createSubtask(Subtask subtask) {
        int id = generateTaskId();
        subtask.setId(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask);
        epic.setStatus(updateEpicStatus(epic));
        subtasks.put(id, subtask);
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        for (Integer subtasksId : epic.getSubtasksIds()) {
            subtasks.remove(subtasksId);
        }
    }

    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtask);
    }

    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    public void updateSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtask);
        epic.addSubtask(subtask);
        epic.setStatus(updateEpicStatus(epic));
    }

    private Status updateEpicStatus(Epic epic) {
        ArrayList<Subtask> subTasks = epic.getSubtasks();
        int doneCount = 0;
        for (Subtask subtask : subTasks) {
            if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
               return Status.IN_PROGRESS;
            } else if (subtask.getStatus().equals(Status.DONE)) {
                doneCount++;
            }
        }
        if (doneCount == subTasks.size()) {
            return Status.DONE;
        }
        else {
            return Status.NEW;
        }
    }

    public ArrayList<Subtask> getSubtasksInEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

}
