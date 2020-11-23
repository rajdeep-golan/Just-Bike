package com.just.justbikev2.Model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Coupon{


    @SerializedName("couponId")
    @Expose
    private String couponId;

    private  String image;

    @SerializedName("couponProfileId")
    @Expose
    private String couponProfileId;

    @SerializedName("couponsCount")
    @Expose
    private int couponsCount;

    @SerializedName("issuedCount")
    @Expose
    private int issuedCount;

    @SerializedName("usedCount")
    @Expose
    private int usedCount;

    private String couponConditionId; // think on this!!

    @SerializedName("couponCreateDate")
    @Expose
    private String couponCreateDate;
    @SerializedName("couponModifiedDate")
    @Expose
    private String couponModifiedDate;

    @SerializedName("couponExpiryDate")
    @Expose
    private String couponExpiryDate;
    @SerializedName("couponIssueDate")
    @Expose
    private String couponIssuedDate;
    String title;
    String info;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Coupon(){

    }

    public Coupon(String title, String info, int couponsCount, String couponIssuedDate, String couponExpiryDate) {
        this.setTitle(title);
        this.setInfo(info);
        this.couponsCount=couponsCount;
        this.couponIssuedDate=couponIssuedDate;
        this.couponExpiryDate=couponExpiryDate;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getCouponProfileId() {
        return couponProfileId;
    }

    public void setCouponProfileId(String couponProfileId) {
        this.couponProfileId = couponProfileId;
    }

    public int getCouponsCount() {
        return couponsCount;
    }

    public void setCouponsCount(int couponsCount) {
        this.couponsCount = couponsCount;
    }

    public int getIssuedCount() {
        return issuedCount;
    }

    public void setIssuedCount(int issuedCount) {
        this.issuedCount = issuedCount;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public String getCouponConditionId() {
        return couponConditionId;
    }

    public void setCouponConditionId(String couponConditionId) {
        this.couponConditionId = couponConditionId;
    }

    public String getCouponCreateDate() {
        return couponCreateDate;
    }

    public void setCouponCreateDate(String couponCreateDate) {
        this.couponCreateDate = couponCreateDate;
    }

    public String getCouponModifiedDate() {
        return couponModifiedDate;
    }

    public void setCouponModifiedDate(String couponModifiedDate) {
        this.couponModifiedDate = couponModifiedDate;
    }

    public String getCouponExpiryDate() {
        return couponExpiryDate;
    }

    public void setCouponExpiryDate(String couponExpiryDate) {
        this.couponExpiryDate = couponExpiryDate;
    }

    public String getCouponIssuedDate() {
        return couponIssuedDate;
    }

    public void setCouponIssuedDate(String couponIssuedDate) {
        this.couponIssuedDate = couponIssuedDate;
    }



    public Coupon(String couponId, String couponProfileId, int couponsCount, int issuedCount, int usedCount, String couponConditionId, String couponCreateDate, String couponModifiedDate, String couponExpiryDate, String couponIssuedDate) {
        this.couponId = couponId;
        this.couponProfileId = couponProfileId;
        this.couponsCount = couponsCount;
        this.issuedCount = issuedCount;
        this.usedCount = usedCount;
        this.couponConditionId = couponConditionId;
        this.couponCreateDate = couponCreateDate;
        this.couponModifiedDate = couponModifiedDate;
        this.couponExpiryDate = couponExpiryDate;
        this.couponIssuedDate = couponIssuedDate;
    }

}
