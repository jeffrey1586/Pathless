package com.example.mini_.pathless;

import java.util.ArrayList;

public class LocationInformation {
    private String location, description;
    private ArrayList<String> images = new ArrayList();

    public LocationInformation(){

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }
}
