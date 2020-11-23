package com.just.justbikev2.Model;

public class PriceHike {
  String startDate;
  String endDate;
  String price;

  public PriceHike() {
  }

  public PriceHike(String startDate, String endDate, String price) {
    this.startDate = startDate;
    this.endDate = endDate;
    this.price = price;
  }

  public String getPrice() {
    return price;
  }

  public void setPrice(String price) {
    this.price = price;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }
}
