package manager.http;

import Tasks.*;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer server;
    private final Gson gson;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = Managers.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handler);
    }

    private void handler(HttpExchange h) {
        try {
            System.out.println("\n/tasks: " + h.getRequestURI());
            final String path = h.getRequestURI().getPath().substring(7); //7
            switch (path) {
                case "" -> {
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/ Ждёт GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    final String response = gson.toJson(taskManager.getPriorTasks());
                    sendText(h, response);
                }
                case "history" -> {
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/history ждёт GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    final String response = gson.toJson(taskManager.getHistory());
                    sendText(h, response);
                }
                case "task" -> handleTask(h);
                case "subtask" -> handleSubtask(h);
                case "subtask/epic" ->  {
                    if (!h.getRequestMethod().equals("GET")) {
                        System.out.println("/subtask/epic Ждёт GET-запрос, а получил: " + h.getRequestMethod());
                        h.sendResponseHeaders(405, 0);
                    }
                    final String query = h.getRequestURI().getQuery();
                    String idParam = query.substring(3);
                    final int id = Integer.parseInt(idParam);
                    final List<Subtask> subtasks = taskManager.getEpicSubtasks(id);
                    final String response = gson.toJson(subtasks);
                    System.out.println("Получили подзадачи эпика id=" + id);
                    sendText(h, response);
                }
                case "epic" -> handleEpic(h);
                default -> {
                    System.out.println("Неизвестный запрос: " + h.getRequestURI());
                    h.sendResponseHeaders(404, 0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            h.close();
        }
    }

    private void handleTask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch ((h.getRequestMethod())) {
            case "GET" -> {
                if (query == null) {
                    final List<Task> tasks = taskManager.getTasks();
                    final String response = gson.toJson(tasks);
                    System.out.println("Получили все задачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Task task = taskManager.getTask(id);
                final String response = gson.toJson(task);
                System.out.println("Получили задачу с id=" + id);
                sendText(h, response);
            }
            case "DELETE" -> {
                if (query == null) {
                    taskManager.deleteTasks();
                    System.out.println("Удалили все задачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.removeTask(id);
                System.out.println("Удалили задачу с id=" + id);
                h.sendResponseHeaders(200, 0);
            }
            case "POST" -> {
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("В Body ничего нет");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Task task = gson.fromJson(json, Task.class);
                final Integer id = task.getId();
                if (id != null) {
                    taskManager.updateTask(task);
                    System.out.println("Обновили задачу с id=" + id);
                    h.sendResponseHeaders(200,0);
                } else {
                    taskManager.addNewTask(task);
                    System.out.println("Создали задачу с id=" + id);
                    final String response = gson.toJson(task);
                    sendText(h, response);
                }
            }
            default -> {
                System.out.println("/task получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405,0);
            }
        }
    }

    private void handleSubtask(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch ((h.getRequestMethod())) {
            case "GET" -> {
                if (query == null) {
                    final List<Subtask> subtasks = taskManager.getSubtasks();
                    final String response = gson.toJson(subtasks);
                    System.out.println("Получили все подзадачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Subtask subtask = taskManager.getSubtask(id);
                final String response = gson.toJson(subtask);
                System.out.println("Получили подзадачу с id=" + id);
                sendText(h, response);
            }
            case "DELETE" -> {
                if (query == null) {
                    taskManager.deleteSubtasks();
                    System.out.println("Удалили все подзадачи");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.removeSubtask(id);
                System.out.println("Удалили подзадачу с id=" + id);
                h.sendResponseHeaders(200, 0);
            }
            case "POST" -> {
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("Body пустой.");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Subtask subtask = gson.fromJson(json, Subtask.class);
                final Integer id = subtask.getId();
                if (id != null) {
                    taskManager.updateSubtask(subtask);
                    System.out.println("Обновили подзадачу с id=" + id);
                    h.sendResponseHeaders(200,0);
                } else {
                    taskManager.addNewSubtask(subtask);
                    System.out.println("Создали подзадачу с id=" + id);
                    final String response = gson.toJson(subtask);
                    sendText(h, response);
                }
            }
            default -> {
                System.out.println("/subtask получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405,0);
            }
        }
    }

    private void handleEpic(HttpExchange h) throws IOException {
        final String query = h.getRequestURI().getQuery();
        switch ((h.getRequestMethod())) {
            case "GET" -> {
                if (query == null) {
                    final List<Epic> epics = taskManager.getEpics();
                    final String response = gson.toJson(epics);
                    System.out.println("Получили все подзадачи");
                    sendText(h, response);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                final Epic epic = taskManager.getEpic(id);
                final String response = gson.toJson(epic);
                System.out.println("Получили эпик с id=" + id);
                sendText(h, response);
            }
            case "DELETE" -> {
                if (query == null) {
                    taskManager.deleteEpics();
                    System.out.println("Удалили все эпики");
                    h.sendResponseHeaders(200, 0);
                    return;
                }
                String idParam = query.substring(3);
                final int id = Integer.parseInt(idParam);
                taskManager.removeEpic(id);
                System.out.println("Удалили эпик с id=" + id);
                h.sendResponseHeaders(200, 0);
            }
            case "POST" -> {
                String json = readText(h);
                if (json.isEmpty()) {
                    System.out.println("Body пустой.");
                    h.sendResponseHeaders(400, 0);
                    return;
                }
                final Epic epic = gson.fromJson(json, Epic.class);
                final Integer id = epic.getId();
                if (id != null) {
                    taskManager.updateEpic(epic);
                    System.out.println("Обновили эпик с id=" + id);
                    h.sendResponseHeaders(200,0);
                } else {
                    taskManager.addNewEpic(epic);
                    System.out.println("Создали эпик с id=" + id);
                    final String response = gson.toJson(epic);
                    sendText(h, response);
                }
            }
            default -> {
                System.out.println("/epic получил: " + h.getRequestMethod());
                h.sendResponseHeaders(405,0);
            }
        }
    }

    public void start() {
        System.out.println("Старт TaskServer на порту " + PORT);
        System.out.println("http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        HttpTaskServer httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();

    }


}
