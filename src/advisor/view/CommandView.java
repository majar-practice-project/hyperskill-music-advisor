package advisor.view;

import java.util.List;
import java.util.Scanner;

public class CommandView {
    private Scanner scanner = new Scanner(System.in);

    public String getInput(){
        return scanner.nextLine();
    }

    public void showNewReleases(List<String> releases){
        System.out.println("---NEW RELEASES---");
        releases.forEach(System.out::println);
    }

    public void showFeatured(List<String> featuredList){
        System.out.println("---FEATURED---");
        featuredList.forEach(System.out::println);
    }

    public void showCategories(List<String> categories){
        System.out.println("---CATEGORIES---");
        categories.forEach(System.out::println);
    }

    public void showPlayList(String category, List<String> playlist){
        System.out.printf("---%s PLAYLISTS---%n", category);
        playlist.forEach(System.out::println);
    }

    public void showExit(){
        System.out.println("---GOODBYE!---");
    }

    public void showInvalidCommand(){
        System.out.println("Invalid input!");
    }
}
