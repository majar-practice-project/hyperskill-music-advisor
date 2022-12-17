package advisor;

import advisor.auth.AuthInfo;
import advisor.auth.AuthServerBuilder;
import advisor.data.*;
import advisor.view.CommandView;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Controller {
    private static final HttpClient client = HttpClient.newBuilder().build();
    private final CommandView view;
    private final Object serverMonitor = new Object();
    private Consumer<PageInfo> viewAction;
    private final HttpServer server = new AuthServerBuilder().setAction(this::verifyToken).build();
    private volatile MusicService musicService;
    private boolean authorized;
    public Controller(CommandView view) {
        this.view = view;
    }

    public void start() {
        processUnauthorizedCommand();
    }

    public void processAuthorizedCommand() {
        while (true) {
            try {
                String[] input = view.getInput().toUpperCase().split(" ");
                switch (input[0]) {
                    case "NEW":
                        processNew();
                        break;
                    case "FEATURED":
                        processFeatured();
                        break;
                    case "CATEGORIES":
                        processCategories();
                        break;
                    case "PLAYLISTS":
                        processPlaylists(Arrays.stream(input).skip(1).collect(Collectors.joining(" ")));
                        break;
                    case "PREV":
                        processPrev();
                        break;
                    case "NEXT":
                        processNext();
                        break;
                    case "EXIT":
                        // hack you ^_^ what a good task description
                        if (viewAction != null) {
                            viewAction = null;
                        } else {
                            view.showExit();
                            System.exit(0);
                        }
                    default:
                        view.showInvalidCommand();
                }
            } catch (APIErrorException e) {
                view.showError(e);
            }
        }
    }

    private void processUnauthorizedCommand() {
        while (true) {
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

    private void authorize() {
        server.start();
        view.showAuthorizeLink();
        synchronized (serverMonitor) {
            try {
                serverMonitor.wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        if (authorized) {
            processAuthorizedCommand();
        }
    }

    private void processNew() throws APIErrorException {
        viewAction = view::showNewReleases;
        viewAction.accept(musicService.getNewRelease());
    }

    private void processFeatured() throws APIErrorException {
        viewAction = view::showFeatured;
        viewAction.accept(musicService.getFeatures());
    }

    private void processCategories() throws APIErrorException {
        viewAction = view::showCategories;
        viewAction.accept(musicService.getCategories());
    }

    private void processPlaylists(String playListName) throws APIErrorException {
        try {
            viewAction = pageInfo -> view.showPlayList(playListName, pageInfo);
            viewAction.accept(musicService.getPlaylist(playListName));
        } catch (InvalidCategoryException e) {
            view.showError(e);
        }

    }

    private void processPrev() throws APIErrorException {
        try {
            viewAction.accept(musicService.getPrev());
        } catch (NoMorePageException | NullPointerException e) {
            view.showNoMorePages();
        }
    }

    private void processNext() throws APIErrorException {
        try {
            viewAction.accept(musicService.getNext());
        } catch (NoMorePageException | NullPointerException e) {
            view.showNoMorePages();
        }
    }

    private void verifyToken(String token) {
        view.showCodeReceived();
        HttpRequest request = HttpRequest.newBuilder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("Authorization", Configuration.getAuthorizationValue())
                .uri(URI.create(String.format("%s/api/token", Configuration.getAuthorizationServerPath())))
                .POST(HttpRequest.BodyPublishers.ofString(String.format("grant_type=authorization_code&redirect_uri=http://localhost:%d&%s", AuthInfo.authPort, token)))
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 400) {
//                System.out.println(response.body());
            } else {
                authorized = true;
                view.showCodeValid();
                JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                musicService = new MusicService(client, responseBody.get("access_token").getAsString());
                server.stop(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            synchronized (serverMonitor) {
                serverMonitor.notify();
            }
        }

    }
}