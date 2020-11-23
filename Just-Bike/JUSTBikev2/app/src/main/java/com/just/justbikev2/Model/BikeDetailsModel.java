package com.just.justbikev2.Model;

import java.io.Serializable;

public class BikeDetailsModel implements Serializable {

    private String bikeImage;
    private String bikeName;

    public BikeDetailsModel(String bikeImage, String bikeName) {
        this.bikeImage = bikeImage;
        this.bikeName = bikeName;
    }

    public BikeDetailsModel() {
    }

    public String getBikeImage() {
        return bikeImage;
    }

    public void setBikeImage(String bikeImage) {
        this.bikeImage = bikeImage;
    }

    public String getBikeName() {
        return bikeName;
    }

    public void setBikeName(String bikeName) {
        this.bikeName = bikeName;
    }
}
