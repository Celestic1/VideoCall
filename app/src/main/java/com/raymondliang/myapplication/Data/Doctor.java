package com.raymondliang.myapplication.Data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Doctor implements Serializable {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String uid;
    private Boolean availability;
    private String device_token;
    private String location;
    private String specialization;
    private String education;
    private String license;
    private String Insurance;
    private String pHistory;
    private String mHistory;
    private String age;

    public Doctor(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Doctor(String Name, String Email, String Phone, String Address, String Location, String Uid, Boolean availability) {
        this.name = Name;
        this.age = "";
        this.email = Email;
        this.phone = Phone;
        this.address = Address;
        this.uid = Uid;
        this.availability = availability;
        this.location = Location;
        this.specialization = "";
        this.education = "";
        this.license = "";
        this.Insurance = "";
        this.pHistory = "";
        this.mHistory = "";
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() { return name; }

    public void setEmail(String email){
        this.email = email;
    }

    public String getEmail(){
        return email;
    }

    public void setPhone(String phone){
        this.phone = phone;
    }

    public String getPhone(){
        return phone;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public String getAddress(){
        return address;
    }

    public String getuid() { return uid; }

    public void setAvailability(Boolean available) { this.availability = available; }

    public Boolean getAvailability() { return availability; }

    public void setDeviceToken(String device_token) { this.device_token = device_token; }

    public String getDeviceToken() { return device_token; }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
