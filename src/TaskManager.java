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

    public int generateTaskId() {
        id++;
        return id;
    }

    public void createTask(String name, String description) {
        int id = generateTaskId();
        Task task = new Task(name, description, id);
        tasks.put(id, task);
    }

    public void createEpic(String name, String description, int id) {
        id = generateTaskId();
        Epic epic = new Epic(name, description, id);
        epics.put(id, epic);
    }

    public void createSubTask(String name, String description, int epicId) {
        int id = generateTaskId();
        Subtask subtask = new Subtask(name, description, id, epicId);
        subtasks.put(id, subtask);
    }

    public void clearTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public void deleteTask(int id) {
       tasks.remove(id);
       epics.remove(id);
       subtasks.remove(id);
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }

    public void getTaskById(int id) {

    }

    public void refreshTask(Task task) {

    }

    public ArrayList<Subtask> getSubtasksByEpic(int epicId) {
        Epic epic = epics.get(epicId);
        ArrayList<Subtask> subtasks = epic.getSubtasksId();
        return subtasks;
    }

}
