package com.raymondliang.myapplication.Data;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {

    private String Name;
    private String Email;
    private String Phone;
    private String Address;
    private String Uid;
    private Boolean Available;
    private String device_token;
    private String Location;
    private String Specialization;
    private String Education;
    private String License;
    private String Insurance;
    private String pHistory;
    private String mHistory;
    private String age;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String Name, String Email, String Phone, String Address, String Location, String Uid, Boolean Available) {
        this.Name = Name;
        this.age = "";
        this.Email = Email;
        this.Phone = Phone;
        this.Address = Address;
        this.Uid = Uid;
        this.Available = Available;
        this.Location = Location;
        this.Specialization = "";
        this.Education = "";
        this.License = "";
        this.Insurance = "";
        this.pHistory = "";
        this.mHistory = "";
    }

    public String getSpecialization() {
        return Specialization;
    }

    public void setSpecialization(String specialization) {
        Specialization = specialization;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        Education = education;
    }

    public String getLicense() {
        return License;
    }

    public void setLicense(String license) {
        License = license;
    }

    public void setName(String name){
        this.Name = name;
    }

    public String getName() { return Name; }

    public void setEmail(String email){
        this.Email = email;
    }

    public String getEmail(){
        return Email;
    }

    public void setPhone(String phone){
        this.Phone = phone;
    }

    public String getPhone(){
        return Phone;
    }

    public void setAddress(String address){
        this.Address = address;
    }

    public String getAddress(){
        return Address;
    }

    public String getuid() { return Uid; }

    public void setAvailability(Boolean available) { this.Available = available; }

    public Boolean getAvailability() { return Available; }

    public void setDeviceToken(String device_token) { this.device_token = device_token; }

    public String getDeviceToken() { return device_token; }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
