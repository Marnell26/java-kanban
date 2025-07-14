package api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.TaskManager;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS:
                handleGetSubtasks(exchange);
                break;
            case GET_SUBTASK:
                handleGetSubtask(exchange);
                break;
            case POST_SUBTASK:
                handlePostSubtask(exchange);
                break;
            case DELETE_SUBTASK:
                handleDeleteSubtask(exchange);
                break;
            case UNKNOWN:
                sendUnknownMethod(exchange);
                break;
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {

    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {

    }

    private void handlePostSubtask(HttpExchange exchange) throws IOException {

    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {

    }

    private void sendUnknownMethod(HttpExchange exchange) throws IOException {

    }

    private Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        switch (requestMethod) {
            case "GET":
                if (pathParts.length == 2) {
                    return Endpoint.GET_SUBTASKS;
                } else {
                    return Endpoint.GET_SUBTASK;
                }
            case "POST":
                return Endpoint.POST_SUBTASK;
            case "DELETE":
                return Endpoint.DELETE_SUBTASK;
            default:
                return Endpoint.UNKNOWN;
        }
    }

    enum Endpoint {GET_SUBTASK, GET_SUBTASKS, POST_SUBTASK, DELETE_SUBTASK, UNKNOWN}

}
