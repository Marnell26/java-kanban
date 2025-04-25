import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, Subtask> subtasks;
    private Integer id = 0;

    public TaskManager() {
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
    }

    public Integer generateTaskId() {
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
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public Task getTaskById(Integer id) {
        return tasks.get(id);
    }

    public Epic getEpicById(Integer id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(Integer id) {
        return subtasks.get(id);
    }

    public void createTask(String name, String description) {
        int id = generateTaskId();
        Task task = new Task(id, name, description);
        tasks.put(id, task);
    }

    public void createEpic(String name, String description) {
        id = generateTaskId();
        Epic epic = new Epic(id, name, description);
        epics.put(id, epic);
    }

    public void createSubTask(String name, String description, Integer epicId) {
        Integer id = generateTaskId();
        Subtask subtask = new Subtask(id, name, description, epicId);
        subtasks.put(id, subtask);
    }

    public void deleteTask(Integer id) {
       tasks.remove(id);
    }

    public void deleteEpic(Integer id) {
        epics.remove(id);
    }

    public void deleteSubtask(Integer id) {
        subtasks.remove(id);
    }

    public void updateTask(Task task) {

    }

    public void updateSubTask() {

    }

    public ArrayList<Subtask> getSubtasksByEpic(Integer epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasks = epic.getSubtasksId();
        return subtasks;
    }

}
