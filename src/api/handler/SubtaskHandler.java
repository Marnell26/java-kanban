package api.handler;

import com.sun.net.httpserver.HttpExchange;
import controller.TaskManager;
import exceptions.TaskIntersectionException;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtaskHandler extends BaseCrudHandler<Task> {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (requestHasId(path)) {
            Optional<Subtask> subtask = taskManager.getSubtaskById(getIdFromRequest(exchange, path));
            if (subtask.isPresent()) {
                sendText(exchange, gson.toJson(subtask.get()));
            } else {
                sendNotFound(exchange, "Подзадача с id " + getIdFromRequest(exchange, path) + " не найдена");
            }
        } else {
            sendText(exchange, gson.toJson(taskManager.getSubtasks()));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] path) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Subtask subtask = gson.fromJson(body, Subtask.class);
        if (requestHasId(path) && taskManager.getSubtaskById(getIdFromRequest(exchange, path)).isPresent()) {
            try {
                taskManager.updateSubtask(subtask);
                sendSuccessful(exchange, "Подзадача с id " + getIdFromRequest(exchange, path) + " успешно обновлена");
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange);
            }
        } else {
            try {
                taskManager.createSubtask(subtask);
                sendSuccessful(exchange, "Подзадача успешно создана");
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange);
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        int id = getIdFromRequest(exchange, path);
        if (taskManager.getSubtaskById(id).isPresent()) {
            taskManager.deleteSubtask(getIdFromRequest(exchange, path));
            sendSuccessful(exchange, "Подзадача с id " + id + " успешно удалена");
        } else {
            sendNotFound(exchange, "Подзадача с id " + getIdFromRequest(exchange, path) + " не найдена");
        }
    }


}
