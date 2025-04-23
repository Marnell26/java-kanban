import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        subtasks = new ArrayList<>();
        this.id = id;
    }
    public Epic (String name, String description, Status status, int id) {
        super(name, description, status);
        this.id = id;
    }

}
