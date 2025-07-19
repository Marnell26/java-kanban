package api;

import com.google.gson.JsonArray;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedHandlerTest extends BaseHttpHandlerTest {


    public PrioritizedHandlerTest() throws IOException {
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
        assertEquals(200, response.statusCode());
        assertEquals(taskManager.getPrioritizedTasks().size(), jsonArray.size());
    }
}
