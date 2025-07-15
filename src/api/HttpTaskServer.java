package api;

import api.handler.*;
import com.sun.net.httpserver.HttpServer;
import controller.Managers;
import controller.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {

        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();

        Task task1 = new Task("Задача1", "Описание1", Duration.ofHours(1), LocalDateTime.of(2025, 1, 1, 0, 0));
        taskManager.createTask(task1);
        Epic epic1 = new Epic("Эпик1", "Описание1");
        taskManager.createEpic(epic1);
        Subtask subtask1 = new Subtask("Подзадача1", "Описание1", epic1.getId(), Duration.ofHours(1),
                LocalDateTime.of(2025, 1, 2, 0, 0));
        taskManager.createSubtask(subtask1);
    }

    public void start() {
        System.out.println("HTTP Task Server started on port " + PORT);
        httpServer.start();
    }

    public void stop() {
        System.out.println("Stopping HTTP Task Server...");
        httpServer.stop(1);
    }
}
