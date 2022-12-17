package advisor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Configuration {

    private static Map<String, String> argMap = new HashMap<>(Map.of(
            "-access", "https://accounts.spotify.com",
            "-resource", "https://api.spotify.com",
            "-page", "5"
    ));
    private static boolean immutable;

    public static void setConfiguration(String[] args){
        if(!immutable){
            Set<String> options = argMap.keySet();
            for(int i=1; i<args.length; i++){
                if(options.contains(args[i-1])){
                    argMap.put(args[i-1], args[i]);
                }
            }
            immutable = true;
        }
    }

    public static String getAuthorizationServerPath(){
        return argMap.get("-access");
    }

    public static String getAPIServerPath(){
        return argMap.get("-resource");
    }

    public static int getPageSize(){
        return Integer.parseInt(argMap.get("-page"));
    }

    public static String getClientId(){
        return "88f602aeb2364916867691cdf0febefb";
    }


    public static String getAuthorizationValue(){
        return "Basic ODhmNjAyYWViMjM2NDkxNjg2NzY5MWNkZjBmZWJlZmI6YzY1ZmNlNWM2Y2I2NGUwOThiMWZkZTlkOTRmZDgzMGY=";
    }
}
