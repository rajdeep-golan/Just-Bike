package com.just.justbikev2.Model;

public class DocumentsModel {

    String name;
    String image;

    public DocumentsModel() {
    }

    public DocumentsModel(String name, String image) {
        this.name = name;
        this.image = image;
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
}
