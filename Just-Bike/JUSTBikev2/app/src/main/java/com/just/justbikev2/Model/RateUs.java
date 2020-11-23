package com.just.justbikev2.Model;

public class RateUs {
  String name;
  String phoneNo;
  String feedback;
  String star;

  public RateUs() {
  }

  public RateUs(String name, String phoneNo, String feedback, String star) {
    this.name = name;
    this.phoneNo = phoneNo;
    this.feedback = feedback;
    this.star = star;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getFeedback() {
    return feedback;
  }

  public void setFeedback(String feedback) {
    this.feedback = feedback;
  }

  public String getStar() {
    return star;
  }

  public void setStar(String star) {
    this.star = star;
  }
}
