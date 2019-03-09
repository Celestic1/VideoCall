package com.raymondliang.myapplication;

import android.widget.ImageView;

public class UserItem {
    private int mImageResource;
    private int availIcon;
    private String mText1;
    private String mText2;

    public UserItem(int imageResource, String text1, int availability, String text2) {
        mImageResource = imageResource;
        mText1 = text1;
        mText2 = text2;
        availIcon = availability;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getText1() {
        return mText1;
    }

    public String getText2() {
        return mText2;
    }

    public int getAvailIcon() { return availIcon; }
}