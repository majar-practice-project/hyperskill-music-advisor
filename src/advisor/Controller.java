package advisor;

import advisor.auth.AuthInfo;
import advisor.auth.AuthServerBuilder;
import advisor.data.MusicService;
import advisor.view.CommandView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Controller {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private final HttpServer server = new AuthServerBuilder().setAction(this::verifyToken).build();
    private final CommandView view;
    private MusicService musicService;

    private final Object serverMonitor = new Object();

    private boolean authorized;

    public Controller(CommandView view) {
        this.view = view;
    }

    public void start() {
        processUnauthorizedCommand();
    }

    public void processAuthorizedCommand() {
        while(true) {
            String[] input = view.getInput().toUpperCase().split(" ");
            switch (input[0]) {
                case "NEW":
                    view.showNewReleases(musicService.getNewRelease());
                    break;
                case "FEATURED":
                    view.showFeatured(musicService.getFeatures());
                    break;
                case "CATEGORIES":
                    view.showCategories(musicService.getCategories());
                    break;
                case "PLAYLISTS":
                    String playListName = Arrays.stream(input).skip(1).collect(Collectors.joining(" "));
                    view.showPlayList(playListName, musicService.getPlaylist(playListName));
                    break;
                case "EXIT":
                    view.showExit();
                    System.exit(0);
                default:
                    view.showInvalidCommand();
            }
        }
    }

    private void processUnauthorizedCommand(){
        while(true) {
            String[] input = view.getInput().toUpperCase().split(" ");
            switch (input[0]) {
                case "AUTH":
                    authorize();
                    break;
                case "EXIT":
                    view.showExit();
                    System.exit(0);
                default:
                    view.showUnauthorized();
            }
        }
    }

    private void authorize(){
        server.start();
        view.showAuthorizeLink();
        synchronized (serverMonitor){
            try {
                serverMonitor.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if(authorized) {
            processAuthorizedCommand();
        }
    }

    private void verifyToken(String token){
        view.showCodeReceived();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", Configuration.getAuthorizationValue())
                .uri(URI.create(String.format("%s/api/token", Configuration.getAuthorizationServerPath())))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("grant_type=authorization_code&redirect_uri=http://localhost:%d&%s", AuthInfo.authPort, token)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() == 400) {
//                System.out.println(response.body());
            } else{
                authorized = true;
                view.showCodeValid();
                JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                musicService = new MusicService(client, responseBody.get("access_token").getAsString());
                server.stop(0);
            }
        } catch (Exception e){
            // -_-
        } finally {
            synchronized (serverMonitor){
                serverMonitor.notify();
            }
        }

    }
}
