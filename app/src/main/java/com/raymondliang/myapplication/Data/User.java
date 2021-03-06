package com.raymondliang.myapplication.Data;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

// TODO: Stop using Serializable, bad practice, on Android use Parcelable
@IgnoreExtraProperties
public class User implements Serializable {
    private static final int CURRENT_VERSION_OF_THIS_OBJECT = 1;

    private static final String VERSION_OF_OBJECT_IN_STORAGE = "version";

    private static final String NAME = "name";
    private static final String EMAIL = "email";
    private static final String PHONE = "phone";
    private static final String ADDRESS = "address";
    private static final String UID = "uid";
    private static final String AVAILABILITY = "availability";
    private static final String DEVICE_TOKEN = "device_token";
    private static final String LOCATION = "location";
    private static final String SPECIALIZATION = "specialization";
    private static final String EDUCATION = "education";
    private static final String LICENSE = "license";
    private static final String INSURANCE = "insurance";
    private static final String PHISTORY = "pHistory";
    private static final String MHISTORY = "mHistory";
    private static final String AGE = "age";

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
    private String insurance;
    private String pHistory;
    private String mHistory;
    private String age;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
        // TODO: Stop doing that
    }

    public User(String name, String email, String phone, String address, String location, String uid, Boolean availability) {
        this.name = name;
        this.age = "";
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.uid = uid;
        this.availability = availability;
        this.location = location;
        this.specialization = "";
        this.education = "";
        this.license = "";
        this.insurance = "";
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

    public JSONObject toJson() {
        try {
            return new JSONObject()
                    .put(VERSION_OF_OBJECT_IN_STORAGE, CURRENT_VERSION_OF_THIS_OBJECT)
                    .put(NAME, name)
                    .put(EMAIL, email)
                    .put(PHONE, phone)
                    .put(ADDRESS, address)
                    .put(UID, uid)
                    .put(AVAILABILITY, availability)
                    .put(DEVICE_TOKEN, device_token)
                    .put(LOCATION, location)
                    .put(SPECIALIZATION, specialization)
                    .put(EDUCATION, education)
                    .put(LICENSE, license)
                    .put(INSURANCE, insurance)
                    .put(PHISTORY, pHistory)
                    .put(MHISTORY, mHistory)
                    .put(AGE, age);
        // TODO: Handle this!
        } catch (JSONException ignored) { return null; }
    }

    public static class JSON_CREATOR {
        public User fromJson(JSONObject jsonObject) {
            try {
                // TODO: When you change this object (and thus its version), you need a new case to parse it
                switch (jsonObject.getInt(VERSION_OF_OBJECT_IN_STORAGE)) {
                    case 1:
                        final String name = jsonObject.optString(NAME);
                        final String email = jsonObject.optString(EMAIL);
                        final String phone = jsonObject.optString(PHONE);
                        final String address = jsonObject.optString(ADDRESS);
                        final String uid = jsonObject.optString(UID);
                        final boolean availability = jsonObject.optBoolean(AVAILABILITY);
                        final String device_token = jsonObject.optString(DEVICE_TOKEN);
                        final String location = jsonObject.optString(LOCATION);
                        final String specialization = jsonObject.optString(SPECIALIZATION);
                        final String education = jsonObject.optString(EDUCATION);
                        final String license = jsonObject.optString(LICENSE);
                        final String insurance = jsonObject.optString(INSURANCE);
                        final String pHistory = jsonObject.optString(PHISTORY);
                        final String mHistory = jsonObject.optString(MHISTORY);
                        final String age = jsonObject.optString(AGE);
                        return new User(name, email, phone, address, location, uid, availability);
                    default:
                        throw new IllegalStateException("Unknown version of item in storage");
                }
            // TODO: Handle this!
            } catch (JSONException ignored) { return null; }
        }
    }
}
