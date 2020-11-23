package com.just.justbikev2.Model;

public class Career {
  private String cv;
  private String phoneNo, role;
  private String aboutYou;

  public Career(String cv, String phoneNo, String role, String aboutYou) {
    this.cv = cv;
    this.phoneNo = phoneNo;
    this.role = role;
    this.aboutYou = aboutYou;
  }

  public Career() {
  }

  public String getAboutYou() {
    return aboutYou;
  }

  public void setAboutYou(String aboutYou) {
    this.aboutYou = aboutYou;
  }

  public String getCv() {
    return cv;
  }

  public void setCv(String cv) {
    this.cv = cv;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getRole() {
    return role;
  }

  public void setRole(String role) {
    this.role = role;
  }
}
