package com.just.justbikev2.Model;

import java.io.Serializable;

public class UserModel implements Serializable {
   private String name;
   private String email;
    private String password;
    private String phone;
    private String isStaff;
    private String secureCode;
    private String homeAddress;

    private String wallet;
    private double lat,lng;

    private String aadhar;
    private String aadharBack;
    private String license;
    private String licesneBack;
    private String additionalDoc;
    private String profilePic;

    private String verified;    //1 : verified ; 0 : not verified

    private String description;

    private String phoneModel;
    private String creationTime;

    public UserModel() {
    }

    public UserModel(String name, String email,String password, String phone,
                     String isStaff, String secureCode, String homeAddress,
                     String wallet, double lat, double lng, String aadhar, String aadharBack,
                     String license, String licesneBack, String profilePic, String verified , String description) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.isStaff = isStaff;
        this.secureCode = secureCode;
        this.homeAddress = homeAddress;
        this.wallet = wallet;
        this.lat = lat;
        this.lng = lng;
        this.aadhar = aadhar;
        this.aadharBack = aadharBack;
        this.license = license;
        this.licesneBack = licesneBack;
        this.profilePic = profilePic;
        this.verified = verified;
        this.description = description;
    }

    public UserModel(String name,String email, String password, String phone, String isStaff, String secureCode, String homeAddress, String profilePic) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.isStaff = isStaff;
        this.secureCode = secureCode;
        this.homeAddress = homeAddress;
        this.profilePic = profilePic;
        wallet = "0";
    }


    public UserModel(String name, String email,String password, String phone,
                     String isStaff, String secureCode, String homeAddress,
                     String aadhar, String aadharBack, String license, String licesneBack,String profilePic,
                        String phoneModel,String creationTime) {
        this.name = name;
      this.email = email;
      this.password = password;
        this.phone = phone;
        this.isStaff = isStaff;
        this.secureCode = secureCode;
        this.homeAddress = homeAddress;
        this.aadhar = aadhar;
        this.aadharBack = aadharBack;
        this.license = license;
        this.licesneBack = licesneBack;
        this.profilePic = profilePic;
        this.phoneModel = phoneModel;
        wallet = "0";
        verified = "0";
        this.creationTime = creationTime;
    }

  public String getAdditionalDoc() {
    return additionalDoc;
  }

  public void setAdditionalDoc(String additionalDoc) {
    this.additionalDoc = additionalDoc;
  }

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  public String getPhoneModel() {
    return phoneModel;
  }

  public void setPhoneModel(String phoneModel) {
    this.phoneModel = phoneModel;
  }

  public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLicesneBack() {
        return licesneBack;
    }

    public void setLicesneBack(String licesneBack) {
        this.licesneBack = licesneBack;
    }


    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
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

    public String getAadharBack() {
        return aadharBack;
    }

    public void setAadharBack(String aadharBack) {
        this.aadharBack = aadharBack;
    }

    public String getAadhar() {
        return aadhar;
    }

    public void setAadhar(String aadhar) {
        this.aadhar = aadhar;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }


    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
