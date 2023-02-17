import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import org.apache.commons.cli.*;

public class App {
    public static void main(String[] args) throws Exception {

        Options options = new Options();

        Option optDatafile = Option.builder("datafile")
                            .argName("datafile")
                            .hasArg()
                            .required()
                            .desc("Tweets input file")
                            .build();
        
        Option optStatefile = Option.builder("statefile")
                            .argName("statefile")
                            .hasArg()
                            .required()
                            .desc("State input file")
                            .build();
        Option optLogfile = Option.builder("logfile")
                            .argName("logfile")
                            .hasArg()
                            .required()
                            .desc("Log output file")
                            .build();

        options.addOption(optDatafile);
        options.addOption(optStatefile);
        options.addOption(optLogfile);

        CommandLine cmd;
        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        try {
            cmd = parser.parse(options, args);
            if(cmd.hasOption("datafile") && cmd.hasOption("statefile") && cmd.hasOption("logfile")){
                String datafile = cmd.getOptionValue("datafile");
                String statefile = cmd.getOptionValue("statefile");
                String logfile = cmd.getOptionValue("logfile");
                List<JSONObject> tweetFile = parse_file(datafile);
                List<JSONObject> stateFile = parse_file(statefile);

                Tweets tweets = new Tweets();
                State states = new State();

                Map<String, List<Double>> fluTweets = tweets.main(tweetFile);
                Map<String, String> fluTweetStates = states.main(fluTweets, stateFile);
                write_file(logfile, fluTweetStates);
                count_occurences(fluTweetStates);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        }

    
    }

    public static List<JSONObject> parse_file(String filename) throws Exception{

        Object jsObj = new JSONParser().parse(new FileReader(filename));

        JSONArray jsArr = (JSONArray) jsObj;

        List<JSONObject> jsList = new ArrayList<>();


        for (Object obj : jsArr){
            jsList.add((JSONObject) obj);
        }

        return jsList;
    }

    public static void write_file(String filename, Map<String, String> fluTweetStates){

        try {
            FileWriter file = new FileWriter(filename);

            for (Object tweet : fluTweetStates.keySet().toArray()){
                String text = (String) tweet;
                String state = fluTweetStates.get(text);
                file.write(state + "\t" + text +"\n");
            }

            file.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void count_occurences(Map<String, String> fluTweetStates){
        List<String> states = new ArrayList<>(fluTweetStates.values());
        Map<String, Integer> stateOccurences = new HashMap<>();

        states.sort(String::compareToIgnoreCase);

        for (String state : states){
            if(stateOccurences.containsKey(state)){
                stateOccurences.replace(state, stateOccurences.get(state) + 1);
            } else{
                stateOccurences.put(state, 1);
            }
        }

        List<String> orderedStateOccurences = new ArrayList<>(stateOccurences.keySet());

        orderedStateOccurences.sort(String::compareToIgnoreCase);

        orderedStateOccurences.forEach((state) -> {
            System.out.println(state + ": " + stateOccurences.get(state));
        });
        
    }

}




