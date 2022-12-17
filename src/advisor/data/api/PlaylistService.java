package advisor.data.api;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.net.http.HttpClient;

public class PlaylistService extends BaseAPIService {
    public PlaylistService(HttpClient client, String ACCESS_TOKEN, String categoryId) {
        super(client, ACCESS_TOKEN, String.format("/v1/browse/categories/%s/playlists", categoryId));
    }

    @Override
    JsonObject getObjectFromJson(String json) {
        return JsonParser.parseString(json).getAsJsonObject().getAsJsonObject("playlists");
    }
}
