package com.just.justbikev2.Model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class BikationModel implements Serializable {
  private String name , details, image1,image2,image3,image4;
  private  String cityId;
  private String timeToSpend , budget , hotelOrCamp , preferredVehicle , location;
  private double lat,lng;

  public BikationModel() {
  }

  public BikationModel(String name, String details, String image1, String image2, String image3, String image4, String cityId, String timeToSpend, String budget, String hotelOrCamp, String preferredVehicle, String location, double lat, double lng) {
    this.name = name;
    this.details = details;
    this.image1 = image1;
    this.image2 = image2;
    this.image3 = image3;
    this.image4 = image4;
    this.cityId = cityId;
    this.timeToSpend = timeToSpend;
    this.budget = budget;
    this.hotelOrCamp = hotelOrCamp;
    this.preferredVehicle = preferredVehicle;
    this.location = location;
    this.lat = lat;
    this.lng = lng;
  }

  public String getCityId() {
    return cityId;
  }

  public void setCityId(String cityId) {
    this.cityId = cityId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDetails() {
    return details;
  }

  public void setDetails(String details) {
    this.details = details;
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

  public String getTimeToSpend() {
    return timeToSpend;
  }

  public void setTimeToSpend(String timeToSpend) {
    this.timeToSpend = timeToSpend;
  }

  public String getBudget() {
    return budget;
  }

  public void setBudget(String budget) {
    this.budget = budget;
  }

  public String getHotelOrCamp() {
    return hotelOrCamp;
  }

  public void setHotelOrCamp(String hotelOrCamp) {
    this.hotelOrCamp = hotelOrCamp;
  }

  public String getPreferredVehicle() {
    return preferredVehicle;
  }

  public void setPreferredVehicle(String preferredVehicle) {
    this.preferredVehicle = preferredVehicle;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public double getLat() {
    return lat;
  }
  public void setLat(double lat) {
    this.lat = lat;
  }

  public double getLng() {
    return lng;
  }
  public void setLng(double lng) {
    this.lng = lng;
  }
}
