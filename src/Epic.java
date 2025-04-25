import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasksId;

    public Epic(Integer id, String name, String description) {
        super(id, name, description);
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
