package advisor.data;

import advisor.Configuration;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicService {
    private final String RESOURCE_ROOT = Configuration.getAPIServerPath();
    private final Map<String, String> categoriesId = new HashMap<>();
    private final HttpClient client;
    private final String ACCESS_TOKEN;

    public MusicService(HttpClient client, String ACCESS_TOKEN) {
        this.client = client;
        this.ACCESS_TOKEN = ACCESS_TOKEN;
    }

    public List<JsonElement> getNewRelease() {
        HttpRequest request = buildGetRequest("/v1/browse/new-releases");
        String json = sendGetRequest(request).body();
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        return jo.getAsJsonObject("albums").getAsJsonArray("items").asList();
    }

    public List<JsonElement> getCategories() {
        HttpRequest request = buildGetRequest("/v1/browse/categories");
        String json = sendGetRequest(request).body();
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();

        List<JsonElement> els = jo.getAsJsonObject("categories").getAsJsonArray("items").asList();
        if(categoriesId.isEmpty()){
            els.forEach(el -> categoriesId.put(el.getAsJsonObject().get("name").getAsString().toUpperCase(), el.getAsJsonObject().get("id").getAsString()));
        }

        return els;
    }

    public List<JsonElement> getFeatures() {
        HttpRequest request = buildGetRequest("/v1/browse/featured-playlists");
        String json = sendGetRequest(request).body();
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();

        return jo.getAsJsonObject("playlists").getAsJsonArray("items").asList();
    }

    public List<JsonElement> getPlaylist(String category) {
        if(categoriesId.isEmpty()) getCategories();
        HttpRequest request = buildGetRequest(String.format("/v1/browse/categories/%s/playlists", categoriesId.get(category)));

        HttpResponse<String> response = sendGetRequest(request);
//        if(response.statusCode() == 404) return null;

        String json = sendGetRequest(request).body();
        JsonObject jo = JsonParser.parseString(json).getAsJsonObject();
        if(jo.has("error")) return List.of(jo.get("error"));

        return jo.getAsJsonObject("playlists").getAsJsonArray("items").asList();
    }

    private HttpResponse<String> sendGetRequest(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpRequest buildGetRequest(String resourcePath) {
        return HttpRequest.newBuilder().header("Authorization", "Bearer " + ACCESS_TOKEN)
                .uri(URI.create(String.format("%s%s", RESOURCE_ROOT, resourcePath)))
                .GET()
                .build();
    }
}