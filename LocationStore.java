package com.example;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Lenovo on 7/30/2016.
 */

public class LocationStore {

    private double latitude;

    private double longitude;

    private Calendar timestamp;

    public Calendar getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Calendar timestamp) {
        this.timestamp = timestamp;
    }

    public LocationStore() {

    }

    public LocationStore(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * @return the latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude
     */
    public double getLongitude() {
        return longitude;
    }
}
