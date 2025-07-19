package api;

import com.google.gson.JsonArray;
import model.Epic;
import model.Task;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicHandlerTest extends BaseHttpHandlerTest {

    public EpicHandlerTest() throws IOException {
    }

    @Test
    void getEpics() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getEpics().size(), jsonArray.size());
    }

    @Test
    void getEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task responseTask = gson.fromJson(response.body(), Epic.class);
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getTaskById(epic1.getId()).get(), responseTask);
    }

    @Test
    void addEpic() throws IOException, InterruptedException {
        Epic newEpic = new Epic("Задача2", "Описание2");
        String requestBody = gson.toJson(newEpic);
        URI uri = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(newEpic.getId()).get(), newEpic);
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        Epic updatedEpic = new Epic(epic1.getId(), "Эпик2", "Новое описание");
        String requestBody = gson.toJson(updatedEpic);
        URI uri = URI.create("http://localhost:8080/epics/" + updatedEpic.getId());
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(taskManager.getTaskById(updatedEpic.getId()).get(), updatedEpic);
    }

    @Test
    void deleteEpic() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/" + epic1.getId());
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
        assertEquals(0, taskManager.getEpics().size());
    }


}
