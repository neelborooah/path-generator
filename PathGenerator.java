package com.example;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class PathGenerator {

    private LocationStore start;
    private LocationStore end;
    private Calendar startTime;

    public PathGenerator(LocationStore start, LocationStore end, Calendar startTime) {
        this.start = start;
        this.end = end;
        this.startTime = startTime;
    }

    public LocationStore getStart() {
        return start;
    }

    public LocationStore getEnd() {
        return end;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public void setEnd(LocationStore end) {
        this.end = end;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setStart(LocationStore start) {
        this.start = start;
    }

    public ArrayList<LocationStore> findPoints() {
        //Make HTTP request for directions
        //Parse response and get list of all points
        //between start and end

        String response = WebLogic.makeRequest(start, end);
        JSONObject json = WebLogic.parseResponse(response);
        ArrayList<LocationStore> givenLocations = new ArrayList<>();
        try {
            JSONObject route = json.getJSONArray("routes").getJSONObject(0);
            JSONObject leg = route.getJSONArray("legs").getJSONObject(0);

            double distance = leg.getJSONObject("distance").getDouble("value");
            int duration = leg.getJSONObject("duration").getInt("value");
            double speed = distance / duration;

            JSONArray steps = json.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
            givenLocations = WebLogic.getAllPoints(steps, startTime);
        } catch(JSONException e) {
            System.out.println("Error parsing data: " + e.toString());
        }

        return givenLocations;
    }

    public static void main(String[] args) {
        LocationStore start = new LocationStore(13.00629, 77.65828);
        LocationStore end = new LocationStore(12.99075, 77.65246);
        Calendar startTime = Calendar.getInstance();
        PathGenerator path = new PathGenerator(start, end, startTime);

        ArrayList<LocationStore> points = path.findPoints();
        for(LocationStore l: points) {
            System.out.println(l.getLatitude() + "," + l.getLongitude());
        }
    }
}
