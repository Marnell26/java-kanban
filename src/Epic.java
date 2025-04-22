import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks;

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
        subtasks = new ArrayList<>();

    }
}
