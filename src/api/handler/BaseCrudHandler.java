package api.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import controller.TaskManager;

import java.io.IOException;

abstract class BaseCrudHandler<T> extends BaseHttpHandler implements HttpHandler {

    public BaseCrudHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String[] path = exchange.getRequestURI().getPath().split("/");

            switch (method) {
                case "GET" -> handleGet(exchange, path);
                case "POST" -> handlePost(exchange, path);
                case "DELETE" -> handleDelete(exchange, path);
                default -> sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (Exception e) {
            sendNotFound(exchange, "Ошибка: " + e.getMessage());
        }
    }

    protected int getIdFromRequest(HttpExchange exchange, String[] path) throws IOException {
        String stringId = path[2];
        try {
            return Integer.parseInt(stringId);
        } catch (NumberFormatException e) {
            sendNotFound(exchange, "Неверный формат запроса");
            return 0;
        }
    }

    protected boolean requestHasId(String[] path) {
        return path.length > 2;
    }

    protected abstract void handleGet(HttpExchange exchange, String[] path) throws IOException;

    protected abstract void handlePost(HttpExchange exchange, String[] path) throws IOException;

    protected abstract void handleDelete(HttpExchange exchange, String[] path) throws IOException;
}