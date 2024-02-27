package com.example.echanjo;

import android.os.Parcel;

public class ChildDetails {
    private String fullname;
    private String doB;
    private String gender;
    private String weight;

     public ChildDetails() {
    }

    protected ChildDetails(Parcel in){
         fullname = in.readString();
         doB = in.readString();
         gender = in.readString();
         weight = in.readString();
    }

    public ChildDetails(String fullname, String doB, String gender, String weight) {
        this.fullname = fullname;
        this.doB = doB;
        this.gender = gender;
        this.weight = weight;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getDoB() {
        return doB;
    }

    public void setDoB(String doB) {
        this.doB = doB;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
