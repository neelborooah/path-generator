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

    public static JSONArray getSteps(JSONObject data) {
        JSONArray steps = new JSONArray();

        try {
            steps = data.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch(JSONException e) {
            System.out.println("Error parsing data: " + e.toString());
        }
        return steps;
    }


}
