package com.just.justbikev2.Model;

public class Article {
    String userId;
    String title;
    String description;
    String image;

    public Article() {
    }

    public Article(String userId, String title,  String description,String image) {
        this.userId = userId;
        this.title = title;
        this.image = image;
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
