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
        Status status = Status.NEW;
        Task task = new Task(name, description, id, status);
        tasks.put(id, task);
    }

    public void createEpic(String name, String description) {
        int id = generateTaskId();
        Status status = Status.NEW;
        Epic epic = new Epic(name, description, id);
        epics.put(id, epic);
    }

    public void createSubTask(String name, String description) {
        int id = generateTaskId();
        Status status = Status.NEW;
        Subtask subtask = new Subtask(name, description, id);
        subtasks.put(id, subtask);
    }

    public void clearTasks() {
        tasks.clear();
        epics.clear();
        subtasks.clear();
    }

    public void deleteTask(int id) {

    }

    public void getTasks() {

    }

    public void getTaskById(int id) {

    }

    public void refreshTask(Task task) {

    }

    public void getSubtasksByEpic() {

    }

}
