package com.example.echanjo;

public class ReadWriteUserDetails {
    public String  fullname,doB, gender, mobile;

    ReadWriteUserDetails() {
    }

    public ReadWriteUserDetails(String textDoB, String textGender, String textMobile) {
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public ReadWriteUserDetails(String fullname, String doB, String gender, String mobile) {
        this.fullname= fullname;
        this.doB = doB;
        this.gender = gender;
        this.mobile = mobile;
    }


}