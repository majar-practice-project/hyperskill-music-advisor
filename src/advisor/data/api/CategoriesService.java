package advisor.data.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpClient;

public class CategoriesService extends BaseAPIService{
    public CategoriesService(HttpClient client, String ACCESS_TOKEN) {
        super(client, ACCESS_TOKEN, "/v1/browse/categories");
    }

    @Override
    JsonObject getObjectFromJson(String json) {
        return JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("categories");
    }
}
