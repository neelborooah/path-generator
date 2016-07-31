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
        JSONArray steps = WebLogic.getSteps(json);
        ArrayList<LocationStore> givenLocations = getAllPoints(steps);

        return givenLocations;
    }

    public ArrayList<LocationStore> getAllPoints(JSONArray steps) {
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

            System.out.println("ETA: " + time_elapsed.getTime());
        } catch(JSONException e) {
            System.out.println("Error parsing data: " + e.toString());
        }

        return givenLocations;
    }

    public static void main(String[] args) {
        LocationStore start = new LocationStore(13.00629, 77.65828);
        LocationStore end = new LocationStore(12.928715, 77.633142);
        Calendar startTime = Calendar.getInstance();
        PathGenerator path = new PathGenerator(start, end, startTime);

        ArrayList<LocationStore> points = path.findPoints();
//        for(LocationStore l: points) {
//            System.out.println(l.getLatitude() + "," + l.getLongitude());
//        }
    }
}
