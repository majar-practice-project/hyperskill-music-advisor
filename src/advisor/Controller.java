package advisor;

import advisor.data.MusicService;
import advisor.view.CommandView;

public class Controller {
    private final CommandView view;
    private final MusicService musicService;

    public Controller(CommandView view, MusicService musicService) {
        this.view = view;
        this.musicService = musicService;
    }

    public void start() {
        processCommand();
    }

    public void processCommand() {
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
                view.showPlayList(input[1], musicService.getPlaylist(input[1]));
                break;
            case "EXIT":
                view.showExit();
                return;
            default:
                view.showInvalidCommand();
        }
        processCommand();
    }
}
