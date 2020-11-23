package com.just.justbikev2.Model;

public class Upi {
   private String upiId;
  private  String city;
  private  String cityId;
  private  double lat;
  private double lng;
  private String addressOfPickUp;
  private String deliveryCost;

  public Upi(String upiId, String city, String cityId, double lat, double lng, String addressOfPickUp, String deliveryCost) {
    this.upiId = upiId;
    this.city = city;
    this.cityId = cityId;
    this.lat = lat;
    this.lng = lng;
    this.addressOfPickUp = addressOfPickUp;
    this.deliveryCost = deliveryCost;
  }

  public Upi() {
    }

  public String getDeliveryCost() {
    return deliveryCost;
  }

  public void setDeliveryCost(String deliveryCost) {
    this.deliveryCost = deliveryCost;
  }

  public String getAddressOfPickUp() {
    return addressOfPickUp;
  }

  public void setAddressOfPickUp(String addressOfPickUp) {
    this.addressOfPickUp = addressOfPickUp;
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

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getCityId() {
    return cityId;
  }

  public void setCityId(String cityId) {
    this.cityId = cityId;
  }

  public String getUpiId() {
        return upiId;
    }

    public void setUpiId(String upiId) {
        this.upiId = upiId;
    }
}
