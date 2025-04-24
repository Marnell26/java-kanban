import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasksId;

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.status = Status.NEW;
        this.subtasksId = new ArrayList<>();
    }

    public ArrayList<Subtask> getSubtasksId() {
        return subtasksId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasksId=" + subtasksId +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
