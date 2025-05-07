package controller;

import model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private int id = 0;
    private HistoryManager historyManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    private int generateTaskId() {
        id++;
        return id;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasks() {
        tasks.clear();
    }

    @Override
    public void clearEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
        subtasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        updateHistory(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        updateHistory(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        updateHistory(subtask);
        return subtask;
    }

    @Override
    public void createTask(Task task) {
        int id = generateTaskId();
        task.setId(id);
        tasks.put(id, task);
    }

    @Override
    public void createEpic(Epic epic) {
        id = generateTaskId();
        epic.setId(id);
        epics.put(id, epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            int id = generateTaskId();
            subtask.setId(id);
            epic.addSubtask(subtask);
            epic.setStatus(updateEpicStatus(epic));
            subtasks.put(id, subtask);
        }
    }

    @Override
    public void deleteTask(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        for (Integer subtasksId : epic.getSubtasksIds()) {
            subtasks.remove(subtasksId);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtask);
    }

    @Override
    public void updateTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            subtasks.put(subtask.getId(), subtask);
            epic.removeSubtask(subtask);
            epic.addSubtask(subtask);
            epic.setStatus(updateEpicStatus(epic));
        }
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

    @Override
    public ArrayList<Subtask> getSubtasksInEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    private void updateHistory(Task task) {
        historyManager.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

}
