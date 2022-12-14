package advisor;

import advisor.data.MusicService;
import advisor.view.CommandView;

public class Main {
    public static void main(String[] args) {
        Controller controller = new Controller(new CommandView(), new MusicService());
        controller.start();
    }
}
