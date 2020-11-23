package com.just.justbikev2.Model;

public class CallLogModel {
  private String phoneNo , contactName , callType , callDate , callTime, callDuration;

  public CallLogModel() {
  }

  public CallLogModel(String phoneNo, String contactName, String callType, String callDate, String callTime, String callDuration) {
    this.phoneNo = phoneNo;
    this.contactName = contactName;
    this.callType = callType;
    this.callDate = callDate;
    this.callTime = callTime;
    this.callDuration = callDuration;
  }

  public String getCallTime() {
    return callTime;
  }

  public void setCallTime(String callTime) {
    this.callTime = callTime;
  }

  public String getCallDuration() {
    return callDuration;
  }

  public void setCallDuration(String callDuration) {
    this.callDuration = callDuration;
  }

  public String getPhoneNo() {
    return phoneNo;
  }

  public void setPhoneNo(String phoneNo) {
    this.phoneNo = phoneNo;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getCallType() {
    return callType;
  }

  public void setCallType(String callType) {
    this.callType = callType;
  }

  public String getCallDate() {
    return callDate;
  }

  public void setCallDate(String callDate) {
    this.callDate = callDate;
  }
}
