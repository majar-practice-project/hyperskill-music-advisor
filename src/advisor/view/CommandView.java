package advisor.view;

import advisor.Configuration;
import advisor.auth.AuthInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class CommandView {
    private Scanner scanner = new Scanner(System.in);

    public String getInput(){
        return scanner.nextLine();
    }

    public void showNewReleases(List<JsonElement> releases){
        System.out.println("---NEW RELEASES---");
        releases.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            System.out.println(obj.get("name").getAsString());
            displayArtists(obj.get("artists").getAsJsonArray().asList());
            System.out.println(obj.getAsJsonObject("external_urls").get("spotify").getAsString());
            System.out.println();
        });
    }

    public void displayArtists(List<JsonElement> els){
        System.out.print("[");
        System.out.print(els.stream()
                .map(el -> el.getAsJsonObject().get("name").getAsString())
                .collect(Collectors.joining(", ")));
        System.out.println("]");
    }

    public void showFeatured(List<JsonElement> featuredList){
        System.out.println("---FEATURED---");
        featuredList.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            System.out.println(obj.get("name").getAsString());
            System.out.println(obj.getAsJsonObject("external_urls").get("spotify").getAsString());
            System.out.println();
        });
    }

    public void showCategories(List<JsonElement> categories){
        System.out.println("---CATEGORIES---");
        categories.forEach(el -> System.out.println(el.getAsJsonObject().get("name").getAsString()));
    }

    public void showPlayList(String category, List<JsonElement> playlist){
        System.out.printf("---%s PLAYLISTS---%n", category);
        if(!playlist.isEmpty()) {
            JsonObject obj = playlist.get(0).getAsJsonObject();
            if(obj.has("message")) {
                System.out.println(obj.get("message").getAsString());
                return;
            }
        }

        playlist.forEach(el -> {
            JsonObject obj = el.getAsJsonObject();
            System.out.println(obj.get("name").getAsString());
            System.out.println(obj.getAsJsonObject("external_urls").get("spotify").getAsString());
            System.out.println();
        });
    }

    public void showExit(){
        System.out.println("---GOODBYE!---");
    }

    public void showInvalidCommand(){
        System.out.println("Invalid input!");
    }

    public void showUnauthorized(){
        System.out.println("Please, provide access for application.");
    }

    public void showAuthorizeLink(){
        System.out.println("use this link to request the access code:");
        System.out.printf("%s/authorize?client_id=%s&redirect_uri=http://localhost:%d&response_type=code%n",
                Configuration.getAuthorizationServerPath(),
                Configuration.getClientId(),
                AuthInfo.authPort);
        System.out.println("waiting for code...");
    }

    public void showCodeValid(){
        System.out.println("Success!");
    }

    public void showCodeReceived() {
        System.out.println("code received");
        System.out.println("Making http request for access_token...");
    }
}
