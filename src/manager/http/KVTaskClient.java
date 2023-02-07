package manager.http;

import manager.file.ManagerSaveException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String url;
    private final String apiToken;

    public KVTaskClient(int port) {
        url = "http://localhost:" + port + "/";
        apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "register"))
                    .GET()
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Не могу осуществить запрос, статус код:" + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу осуществить запрос");
        }
    }

    public String load (String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (response.statusCode() != 200) {
                throw new ManagerSaveException("Не могу осуществить запрос, статус код:" + response.statusCode());
            }
            return response.body();
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу осуществить запрос");
        }
    }

    public void put (String key, String value) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(value))
                    .build();
            HttpResponse<Void> response = null;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.discarding());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (response.statusCode() != 200) {
                 throw new ManagerSaveException("Не могу осуществить запрос, статус код:" + response.statusCode());
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не могу осуществить запрос");
        }
    }
}
