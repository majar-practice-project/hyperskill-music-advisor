package advisor.data.api;

import advisor.Configuration;
import advisor.data.APIErrorException;
import advisor.data.NoMorePageException;
import advisor.data.PageInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseAPIService {
    protected final List<JsonElement> cachedElements = new ArrayList<>();
    private final int pageSize = Configuration.getPageSize();
    private final HttpClient client;
    protected String ACCESS_TOKEN;
    protected String next;
    protected int totalItems = 0;
    protected int currentPage = 0;

    public BaseAPIService(HttpClient client, String ACCESS_TOKEN, String next) {
        this.client = client;
        this.ACCESS_TOKEN = ACCESS_TOKEN;
        this.next = String.format("%s" + next, Configuration.getAPIServerPath());
    }

    public PageInfo getPrev() throws NoMorePageException, APIErrorException {
        if (currentPage == 1) throw new NoMorePageException();
        currentPage--;
        return get();
    }

    public PageInfo getNext() throws NoMorePageException, APIErrorException {
        if (currentPage*pageSize >= totalItems) throw new NoMorePageException();
        currentPage++;
        return get();
    }

    public PageInfo getNew() throws APIErrorException {
        cachedElements.clear();
        currentPage = 1;
        return get();
    }

    private PageInfo get() throws APIErrorException {
        int requiredItems = Math.min(cachedElements.size(), currentPage * pageSize);
        if (cachedElements.size() < currentPage * pageSize && next != null) {
            do {
                HttpRequest request = buildGetRequest(next);
                String json = sendGetRequest(request).body();
                checkForError(json);
                JsonObject root = getObjectFromJson(json);
                next = root.get("next").isJsonNull() ? null : root.get("next").getAsString();

                totalItems = root.get("total").getAsInt();
                requiredItems = Math.min(totalItems, currentPage * pageSize);

                List<JsonElement> els = root.getAsJsonArray("items").asList();
                cachedElements.addAll(els);
            } while (cachedElements.size() < requiredItems);
        }
        return new PageInfo(currentPage,
                totalItems / pageSize + (totalItems%pageSize == 0 ? 0 : 1),
                cachedElements.subList((currentPage - 1) * pageSize, requiredItems));
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
                .uri(URI.create(resourcePath))
                .GET()
                .build();
    }

    private void checkForError(String json) throws APIErrorException {
        if(JsonParser.parseString(json).getAsJsonObject().has("error")){
            throw new APIErrorException(JsonParser.parseString(json).getAsJsonObject()
                    .getAsJsonObject("error")
                    .get("message").getAsString());
        }
    }

    abstract JsonObject getObjectFromJson(String json) throws APIErrorException;
}