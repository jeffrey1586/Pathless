package com.example.mini_.pathless;

public class Post {
    private String description, bitmap, location;
//    private float location;

    public Post() {

    }

    public Post(String description, String bitmap, String location) {
        this.description = description;
        this.bitmap = bitmap;
        this.location = location;
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
