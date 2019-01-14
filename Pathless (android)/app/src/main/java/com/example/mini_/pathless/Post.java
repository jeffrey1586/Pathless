package com.example.mini_.pathless;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;

public class Post {
    private String location, description;
    private ArrayList <String> images;

    public Post() {

    }

    public Post(String location, ArrayList <String> images, String description) {
        this.location = location;
        this.images = images;
        this.description = description;

    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList <String> getImages() {
        return images;
    }

    public void setImages(ArrayList <String> images) {
        this.images = images;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
