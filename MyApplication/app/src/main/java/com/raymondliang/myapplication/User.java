package com.raymondliang.myapplication;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class User implements Serializable {

    private String Name;
    private String Email;
    private String Phone;
    private String Address;

    public User(){
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String Name, String Email, String Phone, String Address) {
        this.Name = Name;
        this.Email = Email;
        this.Phone = Phone;
        this.Address = Address;
    }

    public void setUserName(String name){
        this.Name = name;
    }

    public String getUserName() { return Name; }

    public void setUserEmail(String email){
        this.Email = email;
    }

    public String getUserEmail(){
        return Email;
    }

    public void setUserPhone(String phone){
        this.Phone = phone;
    }

    public String getUserPhone(){
        return Phone;
    }

    public void setUserAddress(String address){
        this.Address = address;
    }

    public String getUserAddress(){
        return Address;
    }
}
