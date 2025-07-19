package api;

import com.google.gson.JsonArray;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskHandlerTest extends BaseHttpHandlerTest {

    public SubtaskHandlerTest() throws IOException {
    }

    @Test
    void getSubtasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getSubtasks().size(), jsonArray.size());
    }

    @Test
    void getSubtask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task responseSubtask = gson.fromJson(response.body(), Subtask.class);
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getSubtaskById(subtask1.getId()).get(), responseSubtask);
    }

    @Test
    void addSubtask() throws IOException, InterruptedException {
        Subtask newSubtask = new Subtask("Подзадача2", "Описание2", epic1.getId(), Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 3, 0, 0));
        String requestBody = gson.toJson(newSubtask);
        int oldSubtasksSize = taskManager.getSubtasks().size();
        URI uri = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(oldSubtasksSize + 1, taskManager.getSubtasks().size());
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        Subtask updatedSubtask = new Subtask(subtask1.getId(), "Подзадача1", "Новое описание", Status.IN_PROGRESS,
                epic1.getId(), Duration.ofHours(1), LocalDateTime.of(2025, 1, 3, 0, 0));
        String requestBody = gson.toJson(updatedSubtask);
        URI uri = URI.create("http://localhost:8080/subtasks/" + updatedSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getSubtaskById(updatedSubtask.getId()).get(), updatedSubtask);
    }

    @Test
    void deleteSubtask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/" + subtask1.getId());
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(0, taskManager.getSubtasks().size());
    }


}
