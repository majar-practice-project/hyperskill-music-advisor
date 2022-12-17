package advisor.data;

import advisor.Configuration;
import advisor.data.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class MusicService {
    private final String RESOURCE_ROOT = Configuration.getAPIServerPath();
    private final Map<String, String> categoriesId = new HashMap<>();
    private final HttpClient client;
    private final String ACCESS_TOKEN;

    private BaseAPIService subService;

    public MusicService(HttpClient client, String ACCESS_TOKEN) {
        this.client = client;
        this.ACCESS_TOKEN = ACCESS_TOKEN;
        try {
            setupCategoriesId();
        } catch (APIErrorException e) {
            throw new RuntimeException("Error fetching categories with API server");
        }
    }

    private void setupCategoriesId() throws APIErrorException {
        BaseAPIService categoryService = new CategoriesService(client, ACCESS_TOKEN);
        PageInfo currentPage = categoryService.getNew();
        currentPage.getEls().forEach(el -> categoriesId.put(el.getAsJsonObject().get("name").getAsString().toUpperCase(), el.getAsJsonObject().get("id").getAsString()));
        try{
            while(true) {
                currentPage = categoryService.getNext();
                currentPage.getEls().forEach(el -> categoriesId.put(el.getAsJsonObject().get("name").getAsString().toUpperCase(), el.getAsJsonObject().get("id").getAsString()));
            }
        } catch (NoMorePageException ignored) {
            // reach the end. We have read the whole category list
        }
    }

    public PageInfo getNewRelease() throws APIErrorException {
        subService = new NewReleaseService(client, ACCESS_TOKEN);
        return subService.getNew();
    }

    public PageInfo getPrev() throws NoMorePageException, APIErrorException {
        if(subService == null) throw new NoMorePageException();
        return subService.getPrev();
    }

    public PageInfo getNext() throws NoMorePageException, APIErrorException {
        if(subService == null) throw new NoMorePageException();
        return subService.getNext();
    }

    public PageInfo getCategories() throws APIErrorException {
        subService = new CategoriesService(client, ACCESS_TOKEN);
        return subService.getNew();
    }

    public PageInfo getFeatures() throws APIErrorException {
        subService = new FeaturedService(client, ACCESS_TOKEN);
        return subService.getNew();
    }

    public PageInfo getPlaylist(String category) throws APIErrorException, InvalidCategoryException {
        if(!categoriesId.containsKey(category)) throw new InvalidCategoryException();
        subService = new PlaylistService(client, ACCESS_TOKEN, categoriesId.get(category));
        return subService.getNew();
    }
}