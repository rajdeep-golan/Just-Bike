package com.just.justbikev2.Model;

public class UserLocationUpdate {
  double lat;
  double lng;
  String phoneModel;
  String time;

  public UserLocationUpdate() {
  }

  public UserLocationUpdate(double lat, double lng, String phoneModel, String time) {
    this.lat = lat;
    this.lng = lng;
    this.phoneModel = phoneModel;
    this.time = time;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
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

  public String getPhoneModel() {
    return phoneModel;
  }

  public void setPhoneModel(String phoneModel) {
    this.phoneModel = phoneModel;
  }
}
