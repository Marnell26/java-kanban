package api;

import api.adapter.DurationAdapter;
import api.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import controller.Managers;
import controller.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseHttpHandlerTest {

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

    public BaseHttpHandlerTest() throws IOException {
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

    @ParameterizedTest
    @ValueSource(strings = {"tasks", "epics", "subtasks"})
    void getRequestsWithoutId(String path) throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/" + path);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JsonArray jsonArray = gson.fromJson(response.body(), JsonArray.class);
        int size = switch (path) {
            case "epics" -> taskManager.getEpics().size();
            case "subtasks" -> taskManager.getSubtasks().size();
            default -> taskManager.getTasks().size();
        };
        assertEquals(200, response.statusCode());
        assertEquals(size, jsonArray.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"tasks/", "epics/", "subtasks/"})
    void getRequestsWithId(String path) throws IOException, InterruptedException {
        String fullPath;
        Task expectedTask;
        Class<? extends Task> taskClass;
        switch (path) {
            case "epics/" -> {
                fullPath = path + epic1.getId();
                expectedTask = taskManager.getEpicById(epic1.getId()).get();
                taskClass = Epic.class;
            }
            case "subtasks/" -> {
                fullPath = path + subtask1.getId();
                expectedTask = taskManager.getSubtaskById(subtask1.getId()).get();
                taskClass = Subtask.class;
            }
            default -> {
                fullPath = path + task1.getId();
                expectedTask = taskManager.getTaskById(task1.getId()).get();
                taskClass = Task.class;
            }
        }
        URI uri = URI.create("http://localhost:8080/" + fullPath);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task responseTask = gson.fromJson(response.body(), taskClass);
        assertEquals(200, response.statusCode());
        assertEquals(expectedTask, responseTask);
    }

    @ParameterizedTest
    @ValueSource(strings = {"tasks", "epics", "subtasks"})
    void addRequests(String path) throws IOException, InterruptedException {
        int oldSize;
        Task newTask;
        switch (path) {
            case "epics":
                oldSize = taskManager.getEpics().size();
                newTask = new Epic("Эпик2", "Описание2");
                break;
            case "subtasks":
                oldSize = taskManager.getSubtasks().size();
                newTask = new Subtask("Подзадача2", "Описание2", epic1.getId(), Duration.ofHours(1),
                        LocalDateTime.of(2025, 1, 3, 0, 0));
                break;
            default:
                oldSize = taskManager.getTasks().size();
                newTask = new Task("Задача2", "Описание", Duration.ofHours(1), LocalDateTime.of(2025, 1, 3, 0, 0));
                break;
        }
        URI uri = URI.create("http://localhost:8080/" + path);
        String requestBody = gson.toJson(newTask);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int newSize = switch (path) {
            case "epics" -> taskManager.getEpics().size();
            case "subtasks" -> taskManager.getSubtasks().size();
            default -> taskManager.getTasks().size();
        };
        assertEquals(201, response.statusCode());
        assertEquals(oldSize + 1, newSize);
    }

    @ParameterizedTest
    @ValueSource(strings = {"tasks/", "epics/", "subtasks/"})
    void updateRequests(String path) throws IOException, InterruptedException {
        String fullPath;
        Task updatedTask;
        switch (path) {
            case "epics/" -> {
                fullPath = path + epic1.getId();
                updatedTask = new Epic(epic1.getId(), "Эпик2", "Новое описание");
            }
            case "subtasks/" -> {
                fullPath = path + subtask1.getId();
                updatedTask = new Subtask(subtask1.getId(), "Подзадача1", "Новое описание", Status.IN_PROGRESS,
                        epic1.getId(), Duration.ofHours(1), LocalDateTime.of(2025, 1, 3, 0, 0));
            }
            default -> {
                fullPath = path + task1.getId();
                updatedTask = new Task(task1.getId(), "Задача1", "Новое описание", Status.NEW, Duration.ofHours(1),
                        LocalDateTime.of(2025, 1, 3, 0, 0));
            }
        }
        String requestBody = gson.toJson(updatedTask);
        URI uri = URI.create("http://localhost:8080/" + fullPath);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .uri(uri)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task expectedTask = switch (path) {
            case "epics/" -> taskManager.getEpicById(updatedTask.getId()).get();
            case "subtasks/" -> taskManager.getSubtaskById(updatedTask.getId()).get();
            default -> taskManager.getTaskById(updatedTask.getId()).get();
        };
        assertEquals(201, response.statusCode());
        assertEquals(expectedTask.getDescription(), updatedTask.getDescription());
    }

    @ParameterizedTest
    @ValueSource(strings = {"tasks/", "epics/", "subtasks/"})
    void deleteRequests(String path) throws IOException, InterruptedException {
        String fullPath = switch (path) {
            case "epics/" -> path + epic1.getId();
            case "subtasks/" -> path + subtask1.getId();
            default -> path + task1.getId();
        };
        URI uri = URI.create("http://localhost:8080/" + fullPath);
        HttpRequest request = HttpRequest.newBuilder().DELETE().uri(uri).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int newSize = switch (path) {
            case "epics/" -> taskManager.getEpics().size();
            case "subtasks/" -> taskManager.getSubtasks().size();
            default -> taskManager.getTasks().size();
        };
        assertEquals(201, response.statusCode());
        assertEquals(0, newSize);
    }


}
