package com.example.echanjo;

public class DataClass {

    private String dataImage;
    private String doB;
    private String fullName;
    private String gender;
    private String weight;
    private String key;

    public DataClass() {
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public void setDoB(String doB) {
        this.doB = doB;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public DataClass(String dataImage, String doB, String fullName, String gender, String weight, String key) {
        this.dataImage = dataImage;
        this.doB = doB;
        this.fullName = fullName;
        this.gender = gender;
        this.weight = weight;
        this.key = key;
    }

    public String getDataImage() {
        return dataImage;
    }

    public String getDoB() {
        return doB;
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public String getWeight() {
        return weight;
    }

    public String getKey() {
        return key;
    }
}
