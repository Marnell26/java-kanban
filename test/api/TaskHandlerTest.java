package api;

import com.google.gson.JsonArray;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskHandlerTest extends BaseHttpHandlerTest {

    public TaskHandlerTest() throws IOException {
    }

    @Test
    void getTasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getTasks().size(), jsonArray.size());
    }

    @Test
    void getTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task responseTask = gson.fromJson(response.body(), Task.class);
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getTaskById(task1.getId()).get(), responseTask);
    }

    @Test
    void addTask() throws IOException, InterruptedException {
        Task newTask = new Task("Задача2", "Описание2", Duration.ofHours(1), LocalDateTime.of(2025, 1, 2, 0, 0));
        String requestBody = gson.toJson(newTask);
        URI uri = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(newTask.getId()).get(), newTask);
    }

    @Test
    void updateTask() throws IOException, InterruptedException {
        Task updatedTask = new Task(1, "Задача2", "Описание2", Status.NEW, Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 2, 0, 0));
        String requestBody = gson.toJson(updatedTask);
        URI uri = URI.create("http://localhost:8080/tasks/" + updatedTask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(updatedTask.getId()).get(), updatedTask);
    }

    @Test
    void deleteTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/" + task1.getId());
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(0, taskManager.getTasks().size());
    }

}
