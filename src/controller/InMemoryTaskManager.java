package controller;

import model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epics;
    protected final Map<Integer, Subtask> subtasks;
    protected int id = 0;
    protected final HistoryManager historyManager = Managers.getDefaultHistory();
    protected final Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
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
        epics.values().forEach(epic -> {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        });
        subtasks.clear();
    }

    @Override
    public Optional<Task> getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            updateHistory(task);
        }
        return Optional.ofNullable(task);
    }

    @Override
    public Optional<Epic> getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            updateHistory(epic);
        }
        return Optional.ofNullable(epic);
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            updateHistory(subtask);
        }
        return Optional.ofNullable(subtask);
    }

    @Override
    public void createTask(Task task) {
        if (!isTaskIntersection(task)) {
            int id = generateTaskId();
            task.setId(id);
            tasks.put(id, task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
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
        if (epic != null && !isTaskIntersection(subtask)) {
            int id = generateTaskId();
            subtask.setId(id);
            epic.addSubtask(subtask);
            epic.setStatus(updateEpicStatus(epic));
            subtasks.put(id, subtask);
            if (subtask.getStartTime() != null && subtask.getDuration() != null) {
                setEpicTimeProperties(epic);
            }
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }
        }
    }

    @Override
    public void deleteTask(int id) {
        prioritizedTasks.removeIf(currentTask -> currentTask.getId() == id);
        tasks.remove(id);
        historyManager.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        prioritizedTasks.removeIf(currentTask -> currentTask.getId() == id);
        epic.getSubtasksIds().forEach(subtasksId -> {
            subtasks.remove(subtasksId);
            historyManager.remove(id);
            prioritizedTasks.removeIf(currentTask -> currentTask.getId() == id);
        });
        historyManager.remove(id);
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasks.remove(id);
        prioritizedTasks.removeIf(currentTask -> currentTask.getId() == id);
        Epic epic = epics.get(subtask.getEpicId());
        epic.removeSubtask(subtask);
        historyManager.remove(id);
        epic.setStatus(updateEpicStatus(epic));
        setEpicTimeProperties(epic);
    }

    @Override
    public void updateTask(Task task) {
        if (!isTaskIntersection(task)) {
            tasks.put(task.getId(), task);
            prioritizedTasks.removeIf(currentTask -> currentTask.getId() == task.getId());
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null && !isTaskIntersection(subtask)) {
            subtasks.put(subtask.getId(), subtask);
            epic.removeSubtask(subtask);
            epic.addSubtask(subtask);
            epic.setStatus(updateEpicStatus(epic));
            prioritizedTasks.removeIf(currentTask -> currentTask.getId() == subtask.getId());
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
                if (subtask.getDuration() != null) {
                    setEpicTimeProperties(epic);
                }
            }
        }
    }

    @Override
    public List<Subtask> getSubtasksInEpic(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    //Вспомогательные методы
    protected int generateTaskId() {
        id++;
        return id;
    }

    protected Status updateEpicStatus(Epic epic) {
        List<Subtask> subTasks = epic.getSubtasks();
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
        } else {
            return Status.NEW;
        }
    }

    protected void updateHistory(Task task) {
        historyManager.add(task);
    }

    private boolean isTasksTimeIntersection(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        return task1.getStartTime().isBefore(task2.getEndTime()) && task2.getStartTime().isBefore(task1.getEndTime());
    }

    private boolean isTaskIntersection(Task newTask) {
        if (newTask.getStartTime() != null) {
            List<Task> prioritizedTasks = getPrioritizedTasks();
            return prioritizedTasks.stream()
                    .anyMatch(task -> isTasksTimeIntersection(newTask, task));
        }
        return false;
    }

    private void setEpicTimeProperties(Epic epic) {
        LocalDateTime startTime = epic.getSubtasks().stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        Duration duration = epic.getSubtasks().stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        LocalDateTime endTime = epic.getSubtasks().stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        epic.setStartTime(startTime);
        epic.setDuration(duration);
        epic.setEndTime(endTime);
    }

}