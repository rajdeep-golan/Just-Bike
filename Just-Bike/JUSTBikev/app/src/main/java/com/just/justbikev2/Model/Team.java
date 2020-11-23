package com.just.justbikev2.Model;

public class Team {
    String name , image , post , gmail , facebook, linkedin;

    public Team() {
    }

    public Team(String name, String image, String post, String gmail, String facebook, String linkedin) {
        this.name = name;
        this.image = image;
        this.post = post;
        this.gmail = gmail;
        this.facebook = facebook;
        this.linkedin = linkedin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getGmail() {
        return gmail;
    }

    public void setGmail(String gmail) {
        this.gmail = gmail;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }
}
