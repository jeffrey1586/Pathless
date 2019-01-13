package com.example.mini_.pathless;

public class Post {
    private String location, description, bitmap;
//    private float location;

    public Post() {

    }

    public Post(String location, String description, String bitmap) {
        this.location = location;
        this.description = description;
        this.bitmap = bitmap;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
