package advisor.data;

import java.util.List;

public class MusicService {
    public List<String> getNewRelease(){
        return List.of("Mountains [Sia, Diplo, Labrinth]",
                "Runaway [Lil Peep]",
                "The Greatest Show [Panic! At The Disco]",
                "All Out Life [Slipknot]");
    }

    public List<String> getCategories(){
        return List.of("Top Lists",
                "Pop",
                "Mood",
                "Latin");
    }

    public List<String> getFeatures(){
        return List.of("Mellow Morning",
                "Wake Up and Smell the Coffee",
                "Monday Motivation",
                "Songs to Sing in the Shower");
    }

    public List<String> getPlaylist(String categories){
        return List.of("Walk Like A Badass  ",
                "Rage Beats  ",
                "Arab Mood Booster  ",
                "Sunday Stroll");
    }


}
