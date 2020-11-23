package com.just.justbikev2.Model;

public class UpcomingBooking {
  String no;
  String bikeName;

  public UpcomingBooking() {
  }

  public UpcomingBooking(String no, String bikeName) {
    this.no = no;
    this.bikeName = bikeName;
  }

  public String getNo() {
    return no;
  }

  public void setNo(String no) {
    this.no = no;
  }

  public String getBikeName() {
    return bikeName;
  }

  public void setBikeName(String bikeName) {
    this.bikeName = bikeName;
  }
}
