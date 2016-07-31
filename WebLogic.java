package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lenovo on 7/30/2016.
 */
public class WebLogic {
    private static String BASE_URL = "http://maps.googleapis.com/maps/api/directions/json";

    public static String getBaseUrl() {

        return BASE_URL;
    }

    public static String generateURL(LocationStore start, LocationStore end) {
        String url = WebLogic.getBaseUrl() + "?origin="+start.getLatitude() + "," + start.getLongitude();
        url += "&destination=" + end.getLatitude() + "," + end.getLongitude();
        return url;
    }

    public static String makeRequest(LocationStore start, LocationStore end) {
        StringBuffer response = new StringBuffer();
        try {
            String url = WebLogic.generateURL(start, end);

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

        } catch(MalformedURLException e) {
            System.out.print("Unable to generate URL");
        } catch(ProtocolException e) {
            System.out.print("Unable to use protocol");
        } catch(IOException e) {
            System.out.print("Unable to read response");
        }
        return response.toString();
    }

    public static JSONObject parseResponse(String response) {
        JSONObject result = null;
        try {
            result = new JSONObject(response);
        } catch(JSONException e) {
            System.out.println("Error parsing data: " + e.toString());
        }

        return result;
    }

    public static ArrayList<LocationStore> getAllPoints(JSONArray steps, Calendar startTime) {
        //Generate a list of all points for a given step in JSON object
        //With the timestamp for each step
        ArrayList<LocationStore> givenLocations = new ArrayList<>();
        Calendar time_elapsed = startTime;
        try {
            for(int i = 0; i < steps.length(); i++) {
                JSONObject step = steps.getJSONObject(i);
                LocationStore start = new LocationStore(step.getJSONObject("start_location").getDouble("lat"),
                        step.getJSONObject("start_location").getDouble("lng"));
                LocationStore end = new LocationStore(step.getJSONObject("end_location").getDouble("lat"),
                        step.getJSONObject("end_location").getDouble("lng"));

                int duration = step.getJSONObject("duration").getInt("value");
                double  distance = step.getJSONObject("distance").getDouble("value"),
                        speed = distance / duration;
                String polyline = step.getJSONObject("polyline").getString("points");

                ArrayList<LocationStore> points = GeoLogic.getCoordsForPolyline(polyline);

                double dist_per_point = distance / points.size();
                int millis = (int) ((dist_per_point / speed) * 1000);
                for(LocationStore l: points) {
                    time_elapsed.add(Calendar.MILLISECOND, millis);
                }

                givenLocations.addAll(points);
//                //Find points in between two steps. Not sure if required.
//                if(i < steps.length() - 1) {
//                    JSONObject nextStartLoc = steps.getJSONObject(i+1).getJSONObject("start_location");
//                    LocationStore nextStart = new LocationStore(nextStartLoc.getDouble("lat"), nextStartLoc.getDouble("lng"));
//                    points = GeoLogic.getCoordsBetweenPoints(end, nextStart);
//                    givenLocations.addAll(points);
//                }
            }
        } catch(JSONException e) {
            System.out.println("Error parsing data: " + e.toString());
        }

        return givenLocations;
    }


}
