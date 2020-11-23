package com.just.justbikev2.Model;

public class PhotoUpload {
    String name , imageUrl;

    public PhotoUpload(String name, String imageUrl) {
        if(name.trim().equals(""))
            name="No Name";
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public PhotoUpload() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
