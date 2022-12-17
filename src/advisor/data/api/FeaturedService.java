package advisor.data.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpClient;

public class FeaturedService extends BaseAPIService{
    public FeaturedService(HttpClient client, String ACCESS_TOKEN) {
        super(client, ACCESS_TOKEN, "/v1/browse/featured-playlists");
    }

    @Override
    JsonObject getObjectFromJson(String json) {
        return JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("playlists");
    }
}
