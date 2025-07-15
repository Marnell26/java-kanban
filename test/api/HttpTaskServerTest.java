package api;

import controller.Managers;
import controller.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServerTest {
    protected TaskManager taskManager = Managers.getDefault();
    protected HttpTaskServer server = new HttpTaskServer(taskManager);
    protected Task task1;
    protected Epic epic1;
    protected Subtask subtask1;

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    public void beforeEach() {
        server.start();

        task1 = new Task("Задача1", "Описание1", Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 0, 0));
        taskManager.createTask(task1);
        epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        subtask1 = new Subtask("Подзадача1", "Описание1", epic1.getId(), Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 2, 0, 0));
        taskManager.createSubtask(subtask1);
    }

    @AfterEach
    void afterEach() throws IOException {
        server.stop();
    }

    @Test
    void getTasksTest() {

    }


}
