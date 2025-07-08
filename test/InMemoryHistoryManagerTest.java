import static org.junit.jupiter.api.Assertions.*;

import controller.*;
import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addTaskToHistory() throws IOException {
        Task task = new Task("Задача1", "Описание задачи 1");
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "После добавления задачи, история не должна быть пустой");
        assertEquals(task, history.getFirst());
    }

    @Test
    void taskShouldBeRemovedFromHistoryWhenDeleted() throws IOException {
        Task task = new Task("Задача 1", "Описание 1");
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.deleteTask(task.getId());
        List<Task> history = taskManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void historyShouldContainOnlyLastTaskView() throws IOException {
        Task task = new Task("Задача 1", "Описание 1");
        taskManager.createTask(task);
        taskManager.getTaskById(task.getId());
        taskManager.getTaskById(task.getId());
        assertEquals(1, taskManager.getHistory().size());
    }



    

}
