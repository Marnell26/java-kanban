package api.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.TaskManager;
import exceptions.TaskIntersectionException;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS:
                handleGetEpics(exchange);
                break;
            case GET_EPIC:
                handleGetEpic(exchange);
                break;
            case GET_EPIC_SUBTASKS:
                handleGetEpicSubtasks(exchange);
                break;
            case POST_EPIC:
                handlePostEpic(exchange);
                break;
            case DELETE_EPIC:
                handleDeleteEpics(exchange);
                break;
            case UNKNOWN:
                sendUnknownMethod(exchange);
                break;
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getEpics()));
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];
        try {
            int id = Integer.parseInt(stringId);
            Epic epic = taskManager.getEpicById(id).orElse(null);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];
        try {
            int id = Integer.parseInt(stringId);
            Epic epic = taskManager.getEpicById(id).orElse(null);
            if (epic != null) {
                sendText(exchange, gson.toJson(epic.getSubtasks()));
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        System.out.println(Arrays.toString(pathParts));
        String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        System.out.println(requestBody);
        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
        System.out.println(jsonObject);
        Epic epic = gson.fromJson(jsonObject, Epic.class);
        System.out.println(epic);

        if (pathParts.length == 2) {
            try {
                taskManager.createEpic(epic);
                sendSuccessful(exchange, "Эпик успешно создан");
            } catch (TaskIntersectionException e) {
                sendHasIntersections(exchange);
            }
        } else {
            String stringId = pathParts[2];
            try {
                int id = Integer.parseInt(stringId);
                try {
                    taskManager.updateEpic(epic);
                    sendSuccessful(exchange, "Эпик с id " + id + " успешно обновлен");
                } catch (TaskIntersectionException e) {
                    sendHasIntersections(exchange);
                }
            } catch (NumberFormatException e) {
                sendNotFound(exchange);
            }
        }
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];
        try {
            int id = Integer.parseInt(stringId);
            if (taskManager.getEpicById(id).isPresent()) {
                taskManager.deleteEpic(id);
                sendSuccessful(exchange, "Эпик с id " + id + " Успешно удален");
            } else {
                sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void sendUnknownMethod(HttpExchange exchange) throws IOException {

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (pathParts.length == 2) {
                    return Endpoint.GET_EPICS;
                } else if (pathParts.length == 3) {
                    return Endpoint.GET_EPIC;
                } else if (pathParts.length == 4) {
                    return Endpoint.GET_EPIC_SUBTASKS;
                } else {
                    return Endpoint.UNKNOWN;
                }
            case "POST":
                return Endpoint.POST_EPIC;
            case "DELETE":
                return Endpoint.DELETE_EPIC;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    enum Endpoint {GET_EPIC, GET_EPICS, GET_EPIC_SUBTASKS, POST_EPIC, DELETE_EPIC, UNKNOWN}

}
