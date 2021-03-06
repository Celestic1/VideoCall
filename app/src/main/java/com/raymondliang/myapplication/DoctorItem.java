package com.raymondliang.myapplication;

import androidx.annotation.DrawableRes;

public class DoctorItem {
    public final String doctorName;
    public final String url;
    @DrawableRes
    public final int drawableIdBackup;
    public final boolean availability;

    public DoctorItem(String doctorName, boolean availability, String url, @DrawableRes int drawableIdBackup) {
        this.doctorName = doctorName;
        this.availability = availability;
        this.url = url;
        this.drawableIdBackup = drawableIdBackup;
    }
}