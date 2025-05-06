import static org.junit.jupiter.api.Assertions.*;

import controller.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class InMemoryHistoryManagerTest {

    private TaskManager taskManager;
    private HistoryManager historyManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void addTaskToHistory() {


        Task task = new Task("Задача1", "Описание задачи 1");
        taskManager.createTask(task);

        taskManager.getTaskById(task.getId());

        historyManager.add(task);

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.getFirst());
    }


}
