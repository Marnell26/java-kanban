package api.handler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.TaskManager;
import model.Epic;
import model.Task;

import java.io.IOException;

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

    private void handlePostEpic(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];
        String requestBody = exchange.getRequestBody().toString();
        JsonObject jsonObject = JsonParser.parseString(requestBody).getAsJsonObject();
    }

    private void handleDeleteEpics(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];
        try {
            int id = Integer.parseInt(stringId);
            taskManager.deleteEpic(id);
            sendSuccessful(exchange, "Эпик с id " + id + " Успешно удален");
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
                } else {
                    return Endpoint.GET_EPIC;
                }
            case "POST":
                return Endpoint.POST_EPIC;
            case "DELETE":
                return Endpoint.DELETE_EPIC;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    enum Endpoint {GET_EPIC, GET_EPICS, POST_EPIC, DELETE_EPIC, UNKNOWN}

}
