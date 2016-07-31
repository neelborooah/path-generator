package com.example;

import java.util.ArrayList;

/**
 * Created by Lenovo on 7/30/2016.
 */
public class GeoLogic {

    private static double distanceInterval = 20; //meters

    private static double earthRadius = 6371000; //meters

    public static double getEarthRadius() {
        return earthRadius;
    }

    public static double getDistanceInterval() {
        return distanceInterval;
    }

    public static double getPathLength(LocationStore start, LocationStore end) {
        //Calculates the length of straight line between two points
        double lat1rads = Math.toRadians(start.getLatitude());
        double lat2rads = Math.toRadians(end.getLatitude());
        double deltaLat = Math.toRadians(end.getLatitude() - start.getLatitude());
        double deltaLng = Math.toRadians(end.getLongitude()-start.getLongitude());
        double a = Math.sin(deltaLat/2) * Math.sin(deltaLat/2) + Math.cos(lat1rads) * Math.cos(lat2rads) * Math.sin(deltaLng/2) * Math.sin(deltaLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = GeoLogic.getEarthRadius() * c;
        return d;
    }

    public static double calculateBearing(LocationStore start, LocationStore end) {
        //calculates the azimuth in degrees from start point to end point
        double  startLat = Math.toRadians(start.getLatitude()),
                startLong = Math.toRadians(start.getLongitude()),
                endLat = Math.toRadians(end.getLatitude()),
                endLong = Math.toRadians(end.getLongitude());

        double dLong = endLong - startLong;
        double dPhi = Math.log(Math.tan(endLat/2.0+Math.PI/4.0)/Math.tan(startLat/2.0+Math.PI/4.0));
        if(Math.abs(dLong) > Math.PI) {
            if(dLong > 0.0) {
                dLong = -(2.0 * Math.PI - dLong);
            } else {
                dLong = (2.0 * Math.PI + dLong);
            }
        }
        double bearing = (Math.toDegrees(Math.atan2(dLong, dPhi)) + 360.0) % 360.0;
        return bearing;
    }

    public static LocationStore getDestinationLatLong(LocationStore start, double azimuth, double distance) {
        //returns the lat an long of destination point
        //given the start lat, long, azimuth, and distance
        double R = GeoLogic.getEarthRadius() / 1000; //Radius of the Earth in km
        double brng = Math.toRadians(azimuth); //Bearing is degrees converted to radians.
        double d = distance/1000, //Distance m converted to km
                lat1 = Math.toRadians(start.getLatitude()), //Current dd lat point converted to radians
                lon1 = Math.toRadians(start.getLongitude()), //Current dd long point converted to radians
                lat2 = Math.asin(Math.sin(lat1) * Math.cos(d/R) + Math.cos(lat1)* Math.sin(d/R)* Math.cos(brng));
        double lon2 = lon1 + Math.atan2(Math.sin(brng) * Math.sin(d/R)* Math.cos(lat1), Math.cos(d/R)- Math.sin(lat1)* Math.sin(lat2));
        //convert back to degrees
        LocationStore destination = new LocationStore(Math.toDegrees(lat2), Math.toDegrees(lon2));
        return destination;
    }

    public static ArrayList<LocationStore> getPointsInBetween(double interval, double azimuth, LocationStore start, LocationStore end) {
        //returns every coordinate pair in between two coordinate
        //pairs given the desired interval
        double d = GeoLogic.getPathLength(start,end);
        double dist = d / interval;
        double counter = (double) interval;
        ArrayList<LocationStore> coords = new ArrayList<>();
        coords.add(start);
        for(int i = 0; i < dist; i++) {
            LocationStore coord = getDestinationLatLong(start, azimuth, counter);
            counter += interval;
            coords.add(coord);
        }

        coords.add(end);
        return coords;
    }

    public static ArrayList<LocationStore> getCoordsForPolyline(String polyline) {
        //Decode Polyline and get points in between points in polyline
        //To generate a list of all points in polyline
        ArrayList<LocationStore> locations = PolylineDecoder.decodePoly(polyline);
        ArrayList<LocationStore> coords = new ArrayList<>();
        for(int i = 0; i < locations.size(); i++) {
            LocationStore l = locations.get(i);
            if(i < locations.size() - 1) {
                coords.addAll(GeoLogic.getCoordsBetweenPoints(l, locations.get(i + 1)));
            }
        }
        return coords;
    }

    public static ArrayList<LocationStore> getCoordsBetweenPoints(LocationStore start, LocationStore end) {
        double azimuth = GeoLogic.calculateBearing(start, end);
        return(GeoLogic.getPointsInBetween(GeoLogic.getDistanceInterval(), azimuth, start, end));
    }
}