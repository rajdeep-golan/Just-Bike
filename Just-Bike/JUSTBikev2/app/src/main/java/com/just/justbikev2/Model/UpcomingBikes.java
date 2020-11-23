package com.just.justbikev2.Model;

import com.just.justbikev2.Common.Common;

import java.util.logging.ConsoleHandler;

public class UpcomingBikes {
    String newBikeImageBig;
    String bikeImageSmall;
    String newBikeCompanyName;
    String newBikeName;
    String expectedDate;
    String expectedCost;
    String details;

    public UpcomingBikes() {
    }

    public UpcomingBikes(String newBikeImageBig, String bikeImageSmall, String newBikeCompanyName, String newBikeName, String expectedDate, String expectedCost, String details) {
        this.newBikeImageBig = newBikeImageBig;
        this.bikeImageSmall = bikeImageSmall;
        this.newBikeCompanyName = newBikeCompanyName;
        this.newBikeName = newBikeName;
        this.expectedDate = expectedDate;
        this.expectedCost = expectedCost;
        this.details = details;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getNewBikeImageBig() {
        return newBikeImageBig;

    }

    public void setNewBikeImageBig(String newBikeImageBig) {
        this.newBikeImageBig = newBikeImageBig;
    }

    public String getBikeImageSmall() {
        return bikeImageSmall;
    }

    public void setBikeImageSmall(String bikeImageSmall) {
        this.bikeImageSmall = bikeImageSmall;
    }

    public String getNewBikeCompanyName() {
        return newBikeCompanyName;
    }

    public void setNewBikeCompanyName(String newBikeCompanyName) {
        this.newBikeCompanyName = newBikeCompanyName;
    }

    public String getNewBikeName() {
        return newBikeName;
    }

    public void setNewBikeName(String newBikeName) {
        this.newBikeName = newBikeName;
    }

    public String getExpectedDate() {
        return expectedDate;
    }

    public void setExpectedDate(String expectedDate) {
        this.expectedDate = expectedDate;
    }

    public String getExpectedCost() {
        return expectedCost;
    }

    public void setExpectedCost(String expectedCost) {
        this.expectedCost = expectedCost;
    }
}
