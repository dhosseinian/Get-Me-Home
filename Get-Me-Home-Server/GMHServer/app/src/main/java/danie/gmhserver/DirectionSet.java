package danie.gmhserver;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by danie on 3/7/2017.
 */
//API Key: AIzaSyBCHPuW0ytKXQW6Ixb4135QdsVgXKmEEGo
//URL: https://maps.googleapis.com/maps/api/directions/json?
public class DirectionSet {

    private final String url = "https://maps.googleapis.com/maps/api/directions/json?";
    private final String key = "AIzaSyBCHPuW0ytKXQW6Ixb4135QdsVgXKmEEGo";

    private String originLat;
    private String originLon;
    private String destination;
    private String preparedUrl;
    private boolean valid;
    private JSONObject directionsJSON = null;
    private ArrayList<Direction> directions;

    public DirectionSet(String request){
        String[] lines = request.split("[\\r\\n]+");
        this.originLat = lines[2];
        this.originLon = lines[3];
        this.destination = lines[1];
        this.preparedUrl = prepareURL(this.originLat, this.originLon, this.destination);
        this.directions = new ArrayList<Direction>();
        this.valid = false;
    }

    public DirectionSet(String originLat, String originLon , String destination){
        this.originLat = originLat;
        this.originLon = originLon;
        this.destination = destination;
        this.preparedUrl = prepareURL(this.originLat, this.originLon, this.destination);
        this.directions = new ArrayList<Direction>();
        this.valid = false;
    }

    //Create URL for Google Maps API request
    private String prepareURL(String originLat, String originLon , String destination){
        destination = destination.replace(' ', '+');
        String fullUrl = url + "origin=" + originLat + "," + originLon + "&destination=" + destination +
                "&mode=driving&sensor=false&language=en-EN&units=imperial" + "&key=" + key;

        return fullUrl;
    }

    //Helper function for readJsonFromUrl
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private static JSONObject readJsonFromUrl(String jsonUrl) throws IOException, JSONException {

        InputStream is = new URL(jsonUrl).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }

        /*
        StringBuilder result = new StringBuilder();
        URL url = new URL(jsonUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();

        Log.d("JSON", result.toString());
        JSONObject jsonObj = new JSONObject(result.toString());
        return jsonObj;
        */
    }

    public void fetchDirections() throws IOException, JSONException {
        this.directionsJSON = readJsonFromUrl(preparedUrl);
        parseDirections();
    }

    private void parseDirections() throws JSONException {
        //Check for validity
        if(!directionsJSON.get("status").equals("OK")){
            valid = false;
            return;
        }
        valid = true;
        JSONArray steps = directionsJSON.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        for(int i = 0; i < steps.length(); i++){
            JSONObject step = steps.getJSONObject(i);

            JSONObject start = step.getJSONObject("start_location");
            double startLat = start.getDouble("lat");
            double startLng = start.getDouble("lng");

            JSONObject end = step.getJSONObject("end_location");
            double endLat = start.getDouble("lat");
            double endLng = start.getDouble("lng");

            String instruction = step.getString("html_instructions").replaceAll("\\<.*?>"," ");
            instruction = instruction.replaceAll("  ", " ");

            directions.add(new Direction(startLat, startLng, endLat, endLng, instruction));
        }
    }

    public String getUrl(){
        return preparedUrl;
    }

    public ArrayList<Direction> getDirections(){
        return directions;
    }

    public  boolean isValid(){
        return valid;
    }

}