package com.just.justbikev2.Model;

public class RatingVehicle {
    String userPhone;
    String userName;
    String rating;
    String comment;
    String vehicleId;

    public RatingVehicle() {
    }

    public RatingVehicle(String userPhone, String userName, String rating, String comment, String vehicleId) {
        this.userPhone = userPhone;
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.vehicleId = vehicleId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }
}
