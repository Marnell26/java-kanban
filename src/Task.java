import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected Status status;

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }
}
