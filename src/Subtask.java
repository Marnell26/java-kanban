public class Subtask extends Task {

    private final int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name, description);
        this.status = Status.NEW;
        this.epicId = epicId;
    }

    public Subtask(String name, String description, Status status, int epicId) {
        super(name, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


}
