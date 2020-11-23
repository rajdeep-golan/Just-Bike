package com.just.justbikev2.Model;

import android.util.Pair;

import java.io.Serializable;
import java.util.List;

public class Vehicle implements Serializable {
    private String currentLocation , destinationLocation , details , name ,parId  , colour;
    private String cost , cc,bhp,tSize,pickup, weight  , year ,mileage;
    private String image1,image2,image3,image4;
    private List<List<Integer>> bookingSlot;   //Pair of startDateTime and endDateTime
    private String company,gif;
    private String keyId;   //Same as key in database
  private String cityId;

    public Vehicle() {
    }

    public Vehicle(String currentLocation, String destinationLocation, String details, String name, String parId, String colour, String cost, String cc, String bhp, String tSize, String pickup, String weight, String year, String mileage, String image1, String image2, String image3, String image4, List<List<Integer>> bookingSlot) {
        this.currentLocation = currentLocation;
        this.destinationLocation = destinationLocation;
        this.details = details;
        this.name = name;
        this.parId = parId;
        this.colour = colour;
        this.cost = cost;
        this.cc = cc;
        this.bhp = bhp;
        this.tSize = tSize;
        this.pickup = pickup;
        this.weight = weight;
        this.year = year;
        this.mileage = mileage;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.image4 = image4;
        this.bookingSlot = bookingSlot;
    }

  public String getCityId() {
    return cityId;
  }

  public void setCityId(String cityId) {
    this.cityId = cityId;
  }

  public String getKeyId() {
    return keyId;
  }

  public void setKeyId(String keyId) {
    this.keyId = keyId;
  }

  public String getGif() {
        return gif;
    }

    public void setGif(String gif) {
        this.gif = gif;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getBhp() {
        return bhp;
    }

    public void setBhp(String bhp) {
        this.bhp = bhp;
    }

    public String gettSize() {
        return tSize;
    }

    public void settSize(String tSize) {
        this.tSize = tSize;
    }

    public List<List<Integer>> getBookingSlot() {
        return bookingSlot;
    }

    public void setBookingSlot(List<List<Integer>> bookingSlot) {
        this.bookingSlot = bookingSlot;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public String getImage3() {
        return image3;
    }

    public void setImage3(String image3) {
        this.image3 = image3;
    }

    public String getImage4() {
        return image4;
    }

    public void setImage4(String image4) {
        this.image4 = image4;
    }


    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParId() {
        return parId;
    }

    public void setParId(String parId) {
        this.parId = parId;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }
}
