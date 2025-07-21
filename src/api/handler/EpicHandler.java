package api.handler;

import com.sun.net.httpserver.HttpExchange;
import controller.TaskManager;
import exceptions.TaskIntersectionException;
import model.Epic;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class EpicHandler extends BaseCrudHandler<Task> {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    protected void handleGet(HttpExchange exchange, String[] path) throws IOException {
        if (requestHasId(path)) {
            Optional<Epic> epic = taskManager.getEpicById(getIdFromRequest(exchange, path));
            if (epic.isPresent() && path.length == 4) {
                sendText(exchange, gson.toJson(taskManager.getSubtasksInEpic(epic.get().getId())));
            } else if (epic.isPresent()) {
                sendText(exchange, gson.toJson(epic.get()));
            } else {
                sendNotFound(exchange, "Эпик с id " + getIdFromRequest(exchange, path) + " не найден");
            }
        } else {
            sendText(exchange, gson.toJson(taskManager.getEpics()));
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange, String[] path) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (requestHasId(path) && taskManager.getEpicById(getIdFromRequest(exchange, path)).isPresent()) {
            try {
                taskManager.updateEpic(epic);
                sendSuccessful(exchange, "Эпик с id " + epic.getId() + " успешно обновлена");
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange);
            }
        } else {
            try {
                taskManager.createEpic(epic);
                sendSuccessful(exchange, "Эпик успешно создан");
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange);
            }
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange, String[] path) throws IOException {
        int id = getIdFromRequest(exchange, path);
        if (taskManager.getEpicById(id).isPresent()) {
            taskManager.deleteEpic(getIdFromRequest(exchange, path));
            sendSuccessful(exchange, "Эпик с id " + id + " успешно удален");
        } else {
            sendNotFound(exchange, "Эпик с id " + getIdFromRequest(exchange, path) + " не найден");
        }
    }

}
