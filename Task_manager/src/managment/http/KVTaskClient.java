package managment.http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {
    private final HttpClient httpClient;
    private final URI url;


    public KVTaskClient(String url) {
        this.url = URI.create(url);
        httpClient = HttpClient.newHttpClient();
    }
    
    public String getApiToken () {
        URI url = URI.create(this.url.toString() + "/register");
        HttpRequest request = HttpRequest.newBuilder().uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;

    }

    public void put(String key, String json) {
        String token = getApiToken();
        URI url = URI.create(this.url + "/save/" + key + "?API_TOKEN=" +token);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                System.out.println("Сохранение успешно!");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String load(String key) {
        String token = getApiToken();
        String request = "null";
        URI url = URI.create(this.url + "/load/" + key + "?API_TOKEN=" + token);
        HttpRequest httpRequest = HttpRequest
                .newBuilder()
                .uri(url)
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                request = response.body();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return request;
    }

    public URI getUrl() {
        return url;
    }
}
