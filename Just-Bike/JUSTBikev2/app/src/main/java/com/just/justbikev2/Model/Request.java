package com.just.justbikev2.Model;

import java.util.List;

public class Request {
    private String phone;
    private String address;
    private String purpose;
    private String name;
    private List<Order> orderList;
    private String total;
    private String comment;
    private String status;    //0: placed  1: Still time to reach you  2: Bike Sent  3:Journey On  4: Journey Over 5: Cancelled
    private String paymentState;
    private String LatLng;
    private String outstandingAmt,discount;
    private String paymentMethod;
    private String startDate;
    private String endDate;
    private String pdf;
    private int noOfHelmets;
  private int selfPickup;
  private int selfDrop;
  public double currentLat;
    public double currentLng;
    public Request() {
    }

    public Request(String phone, String address,String purpose, String name, List<Order> orderList,
                   String total, String comment, String status, String paymentState,
                   String latLng, String outstandingAmt, String discount ,String paymentMethod,
                   String startDate, String endDate ,int noOfHelmets,
                  int selfPickup, int selfDrop) {
        this.phone = phone;
        this.address = address;
        this.purpose = purpose;
        this.name = name;
        this.orderList = orderList;
        this.total = total;
        this.comment = comment;
        this.status = status;
        this.paymentState = paymentState;
        LatLng = latLng;
        this.outstandingAmt = outstandingAmt;
        this.discount = discount;
        this.paymentMethod = paymentMethod;
        this.startDate = startDate;
        this.endDate = endDate;
      this.noOfHelmets = noOfHelmets;
      this.selfPickup = selfPickup;
      this.selfDrop = selfDrop;
    }

    public Request(String phone, String address,
                   String name, List<Order> orderList, String total, String comment,
                   String status, String paymentState, String latLng, String outstandingAmt, String paymentMethod) {
        this.phone = phone;
        this.address = address;
        this.name = name;
        this.orderList = orderList;
        this.total = total;
        this.comment = comment;
        this.status = status;
        this.paymentState = paymentState;
        LatLng = latLng;
        this.outstandingAmt = outstandingAmt;
        this.paymentMethod = paymentMethod;
    }

  public int getSelfPickup() {
    return selfPickup;
  }

  public void setSelfPickup(int selfPickup) {
    this.selfPickup = selfPickup;
  }

  public int getSelfDrop() {
    return selfDrop;
  }

  public void setSelfDrop(int selfDrop) {
    this.selfDrop = selfDrop;
  }

  public String getPdf() {
    return pdf;
  }

  public void setPdf(String pdf) {
    this.pdf = pdf;
  }

  public String getDiscount() {
    return discount;
  }

  public void setDiscount(String discount) {
    this.discount = discount;
  }

  public int getNoOfHelmets() {
    return noOfHelmets;
  }

  public void setNoOfHelmets(int noOfHelmets) {
    this.noOfHelmets = noOfHelmets;
  }

  public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
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

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOutstandingAmt() {
        return outstandingAmt;
    }

    public void setOutstandingAmt(String outstandingAmt) {
        this.outstandingAmt = outstandingAmt;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public void setOrderList(List<Order> orderList) {
        this.orderList = orderList;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentState() {
        return paymentState;
    }

    public void setPaymentState(String paymentState) {
        this.paymentState = paymentState;
    }

    public String getLatLng() {
        return LatLng;
    }

    public void setLatLng(String latLng) {
        LatLng = latLng;
    }
}
