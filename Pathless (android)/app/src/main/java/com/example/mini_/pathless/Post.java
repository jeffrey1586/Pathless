package com.example.mini_.pathless;

import java.util.ArrayList;

public class Post {
    private String location, description;
    private ArrayList <String> urls;
    private ArrayList coordinates;

    public Post() {

    }

    public Post(String location, ArrayList <String> urls, String description,
                ArrayList coordinates) {
        this.location = location;
        this.urls = urls;
        this.description = description;
        this.coordinates = coordinates;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList <String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList <String> urls) {
        this.urls = urls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(ArrayList coordinates) {
        this.coordinates = coordinates;
    }
}
