import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class Tweets {
    public  Map<String, List<Double>> main(List<JSONObject> tweets_list){

        Map<String, List<Double>> tweets = get_tweet_contents(tweets_list);
        tweets = identify_flu(tweets);

        return tweets;
    }

    public static  Map<String, List<Double>> get_tweet_contents(List<JSONObject> tweets){
        // Setting up Map to provide a relationship between the texts and location
        Map<String, List<Double>> tweet_contents = new HashMap<>();
        
        for (JSONObject tweet : tweets){
            // Set up blank texts and null locations for error handling
            String content = "";
            List<Double> location = new ArrayList<>();
            // Check if text is a null value
            if (tweet.get("text")!= null){
                // replace the content placeholder
                content = (String) tweet.get("text");
            } 
            // Check if the location is anull value
            if(tweet.get("location") != null){
                JSONArray location_js =  (JSONArray) tweet.get("location");
                // Check is th size of the coordinates
                if (location_js.size() == 2){
                    // Check if the coordinates are null values
                    if(location_js.get(0) != null && location_js.get(1) != null){
                        Double latitude = (Double) location_js.get(0);
                        Double longitude = (Double) location_js.get(1);
                        // Add to the list for the location
                        location.add(latitude);
                        location.add(longitude);
                    }

                }
            }
            // put the tweet and location into the Map
            tweet_contents.put(content, location);
        }
        return tweet_contents;
    }
    
    public static Map<String, List<Double>> identify_flu(Map<String, List<Double>> tweets){
        // Set of patterns to search in the tweets
        Pattern pattern_1 = Pattern.compile("\\p{Blank}flu\\p{Blank}", Pattern.CASE_INSENSITIVE);
        Pattern pattern_2 = Pattern.compile("flu\\p{Blank}", Pattern.CASE_INSENSITIVE);
        Pattern pattern_3 = Pattern.compile("\\p{Blank}flu\\p{Punct}", Pattern.CASE_INSENSITIVE);
        Pattern pattern_4 = Pattern.compile("#flu", Pattern.CASE_INSENSITIVE);

        for (Object text : tweets.keySet().toArray()){
            String content = text.toString();
            Matcher match_1 = pattern_1.matcher(content);
            Matcher match_2 = pattern_2.matcher(content);
            Matcher match_3 = pattern_3.matcher(content);
            Matcher match_4 = pattern_4.matcher(content);
            if (!match_1.find() &&  !match_2.find()  && !match_3.find() && !match_4.find()){
                tweets.remove(text);
            }
        }

        return tweets;
    }
}
