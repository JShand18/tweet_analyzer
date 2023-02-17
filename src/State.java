import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import org.json.simple.JSONObject;

public class State {
    public Map<String, String> main(Map<String, List<Double>> flu_tweets, List<JSONObject> states_list) {

        Map<List<Double>, String> states = location_to_state_mapping(states_list);
        Map<String, String> flu_states = locate_flu(flu_tweets, states);
        
        return flu_states;
    }

    public Map<List<Double>, String> location_to_state_mapping(List<JSONObject> states_list){

        Map<List<Double>, String> state_coords = new HashMap<>();

        for (JSONObject state : states_list){
            String state_name = "";
            List<Double> location = new ArrayList<>();

            if (state.get("name")!= null){
                state_name = (String) state.get("name");
            } 
            if(state.get("latitude") != null && state.get("longitude") != null){
                Double latitude = (Double) state.get("latitude");
                Double longitude = (Double) state.get("longitude");
                location.add(latitude);
                location.add(longitude);
            }
            state_coords.put(location, state_name);
        }
        return state_coords;
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> locate_flu(Map<String, List<Double>> flu_tweets, Map<List<Double>, String> states){
        // Initalize HashMap that will contain the tweet text and state of origin
        Map<String, String> flu_states = new HashMap<>();

        // Loop through the tweets
        for(Object tweet : flu_tweets.keySet().toArray()){
            String text = (String) tweet;
            // Get the tweet latitude and longitude
            List<Double> coords = flu_tweets.get(text);
            Double tweet_lat = coords.get(0);
            Double tweet_long = coords.get(1);

            // Setting temporary comparison values to determine closest state capital
            Double min_distance = 100000000.0;
            String min_state = "";

            // Loop through the state coords to determin closest state
            for(Object state_coords : states.keySet().toArray()){
                // retriving state latitude and longitude
                List<Double> state = (List<Double>) state_coords;
                Double state_lat = (Double) state.get(0);
                Double state_long = (Double) state.get(1);
                // Euclid distance calculation
                Double distance = Math.sqrt((Math.pow((state_lat - tweet_lat), 2) + Math.pow((state_long - tweet_long), 2)));
                // Distance comparsion to find closest match
                if (distance < min_distance){
                    min_state = states.get(state_coords);
                    min_distance = distance;
                }
            }
            // Placing tweets mapped to closest state
            flu_states.put(text, min_state);
        }
        return flu_states;
    }
}
