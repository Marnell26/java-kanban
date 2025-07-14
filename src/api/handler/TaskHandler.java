package api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.TaskManager;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS:
                handleGetTasks(exchange);
                break;
            case GET_TASK:
                handleGetTask(exchange);
                break;
            case POST_TASK:
                handlePostTask(exchange);
                break;
            case DELETE_TASK:
                handleDeleteTask(exchange);
                break;
            case UNKNOWN:
                sendUnknownMethod(exchange);
                break;
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        sendText(exchange, gson.toJson(taskManager.getTasks()));
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        String stringId = exchange.getRequestURI().getPath().split("/")[2];
        try {
            int id = Integer.parseInt(stringId);
            if (taskManager.getTaskById(id).isEmpty()) {
                sendNotFound(exchange);
            } else {
                sendText(exchange, gson.toJson(task));
            }
        } catch (NumberFormatException e) {
            sendNotFound(exchange);
        }
    }

    private void handlePostTask(HttpExchange exchange) throws IOException {
        taskManager.createTask();
        taskManager.updateTask();
        sendText(exchange, );
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        taskManager.deleteTask();
        sendText(exchange, );
    }

    private void sendUnknownMethod(HttpExchange exchange) throws IOException {
        sendText(exchange, );
    }


    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (pathParts.length == 2) {
                    return Endpoint.GET_TASKS;
                } else {
                    return Endpoint.GET_TASK;
                }
            case "POST":
                return Endpoint.POST_TASK;
            case "DELETE":
                return Endpoint.DELETE_TASK;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    enum Endpoint {GET_TASK, GET_TASKS, POST_TASK, DELETE_TASK, UNKNOWN}

}
