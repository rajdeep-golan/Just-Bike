package com.just.justbikev2.Model;

public class AadharCard
{
    public String uid;
    public String name;
    public String gender;
    public String yob;
    public String co;
    public String house;
    public String lm;
    public String loc;
    public String vtc;
    public String po;
    public String dist;
    public String subdist;
    public String state;
    public String pincode;
    public String dob;
    public String originalXML;


    public AadharCard() {
    }

    public AadharCard(String uid, String name, String gender, String yob, String co, String house, String lm, String loc, String vtc, String po, String dist, String subdist, String state, String pincode, String dob, String originalXML) {
        this.uid = uid;
        this.name = name;
        this.gender = gender;
        this.yob = yob;
        this.co = co;
        this.house = house;
        this.lm = lm;
        this.loc = loc;
        this.vtc = vtc;
        this.po = po;
        this.dist = dist;
        this.subdist = subdist;
        this.state = state;
        this.pincode = pincode;
        this.dob = dob;
        this.originalXML = originalXML;
    }

    public String getFormattedUID() {
        if (uid.length() == 12) {
            String newUIDString = uid.substring(0, 4) + " " + uid.substring(4, 8) + " " + uid.substring(8, 12);
            return newUIDString;
        }
        return uid;
    }

    public String getAddress() {
        return house + ", " + lm + ", " + loc + ", " + dist + ", " + subdist + ", " + state + ".\nPincode: " + pincode;
    }
}