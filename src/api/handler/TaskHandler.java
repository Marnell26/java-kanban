package api.handler;

import com.sun.net.httpserver.HttpExchange;
import controller.TaskManager;
import exceptions.TaskIntersectionException;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TaskHandler extends BaseCrudHandler<Task> {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (requestHasId(path)) {
            Optional<Task> task = taskManager.getTaskById(getIdFromRequest(exchange, path));
            if (task.isPresent()) {
                sendText(exchange, gson.toJson(task.get()));
            } else {
                sendNotFound(exchange, "Задача с id " + getIdFromRequest(exchange, path) + " не найдена");
            }
        } else {
            sendText(exchange, gson.toJson(taskManager.getTasks()));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] path) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        if (requestHasId(path) && taskManager.getTaskById(getIdFromRequest(exchange, path)).isPresent()) {
            try {
                taskManager.updateTask(task);
                sendSuccessful(exchange, "Задача с id " + getIdFromRequest(exchange, path) + " успешно обновлена");
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange);
            }
        } else {
            try {
                taskManager.createTask(task);
                sendSuccessful(exchange, "Задача успешно создана");
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange);
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        int id = getIdFromRequest(exchange, path);
        if (taskManager.getTaskById(id).isPresent()) {
            taskManager.deleteTask(getIdFromRequest(exchange, path));
            sendSuccessful(exchange, "Задача с id " + id + " успешно удалена");
        } else {
            sendNotFound(exchange, "Задача с id " + getIdFromRequest(exchange, path) + " не найдена");
        }
    }

}
