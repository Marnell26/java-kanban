package api;

import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controller.Managers;
import controller.TaskManager;
import model.Epic;
import model.Status;
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
    public void beforeEach() {
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
    void afterEach() throws IOException {
        client.close();
        server.stop();
    }

    @Test
    public void checkRunningServer() throws IOException, InterruptedException {

        try (HttpClient client = HttpClient.newHttpClient()) {
            URI uri = URI.create("http://localhost:8080/");
            HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            assertEquals(404, response.statusCode());
        }
    }

    @Test
    void getTasksTest() {

    }

    @Test
    public void addTask_Update() throws IOException, InterruptedException {
        Task updatedTask = new Task(task1.getId(), "Измененное имя", "Измененное описание", Status.DONE,
                task1.getDuration(), task1.getStartTime());
        String requestBody = gson.toJson(updatedTask);

        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, taskManager.getTasks().size());
        assertEquals("Измененное имя", taskManager.getTaskById(1).get().getName());
        assertEquals(Status.DONE, taskManager.getTaskById(1).get().getStatus());

        client.close();
    }

    @Test
    public void deleteTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
    }


}
