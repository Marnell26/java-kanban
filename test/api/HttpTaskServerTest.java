package api;

import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Managers;
import controller.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    protected TaskManager taskManager = Managers.getDefault();
    protected HttpTaskServer server = new HttpTaskServer(taskManager);
    protected HttpClient client;
    protected Task task1;
    protected Epic epic1;
    protected Subtask subtask1;
    protected Gson gson = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();

    public HttpTaskServerTest() throws IOException {
    }

    @BeforeEach
    protected void beforeEach() {
        server.start();
        client = HttpClient.newHttpClient();

        task1 = new Task("Задача1", "Описание1", Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 0, 0));
        taskManager.createTask(task1);
        epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        subtask1 = new Subtask("Подзадача1", "Описание1", epic1.getId(), Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 2, 0, 0));
        taskManager.createSubtask(subtask1);
    }

    @AfterEach
    protected void afterEach() throws IOException {
        client.close();
        server.stop();
    }

    @Test
    public void checkRunningServer() {
        try {
            URI uri = URI.create("http://localhost:8080/");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(404, response.statusCode());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
