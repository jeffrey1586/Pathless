package com.example.mini_.pathless;

import java.util.HashMap;

public class MarkerInformation {
    private String location;
    private HashMap<String, Double> coordinates;

    public MarkerInformation(){

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public HashMap<String, Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(HashMap<String, Double> coordinates) {
        this.coordinates = coordinates;
    }
}
