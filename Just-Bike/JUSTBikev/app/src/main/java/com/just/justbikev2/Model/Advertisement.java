package com.just.justbikev2.Model;

public class Advertisement {
    String gifImage;
    String gifText;

    public Advertisement() {
    }

    public Advertisement(String gifImage, String gifText) {
        this.gifImage = gifImage;
        this.gifText = gifText;
    }

    public String getGifImage() {
        return gifImage;
    }

    public void setGifImage(String gifImage) {
        this.gifImage = gifImage;
    }

    public String getGifText() {
        return gifText;
    }

    public void setGifText(String gifText) {
        this.gifText = gifText;
    }
}
