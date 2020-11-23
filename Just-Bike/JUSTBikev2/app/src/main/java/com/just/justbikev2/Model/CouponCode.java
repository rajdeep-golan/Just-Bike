package com.just.justbikev2.Model;

public class CouponCode {
    String amount;
    String code;

    public CouponCode(String amount, String code) {
        this.amount = amount;
        this.code = code;
    }

    public CouponCode() {
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
