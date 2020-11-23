package com.just.justbikev2.Model;

public class FranchiseModel {
  String location;
  private String phoneNo, cv;
  private String aboutYou;

  public FranchiseModel() {
  }

  public FranchiseModel(String location, String phoneNo, String cv, String aboutYou) {
    this.location = location;
    this.phoneNo = phoneNo;
    this.cv = cv;
    this.aboutYou = aboutYou;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getCv() {
    return cv;
  }

  public void setCv(String cv) {
    this.cv = cv;
  }

  public String getAboutYou() {
    return aboutYou;
  }

  public void setAboutYou(String aboutYou) {
    this.aboutYou = aboutYou;
  }
}
