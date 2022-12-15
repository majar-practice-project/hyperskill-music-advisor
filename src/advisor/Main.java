package advisor;

import advisor.view.CommandView;

public class Main {
    public static void main(String[] args) {
        Configuration.setConfiguration(args);
        Controller controller = new Controller(new CommandView());
        controller.start();
    }
}
