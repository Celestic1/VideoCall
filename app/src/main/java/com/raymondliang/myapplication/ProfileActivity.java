package com.raymondliang.myapplication;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.raymondliang.myapplication.Data.User;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView mName, mEmail, mPhone, mLocation, mLicense, mEducation, mSpecialization;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getDoctorViews();
        updateUserProfile();
    }

    public void updateUserProfile(){

        // retrieves object passed through intent
        Intent i = getIntent();
        User user = (User)i.getSerializableExtra("User info");
        mName.setText(user.getName());
        mEmail.setText(user.getEmail());
        mPhone.setText(user.getPhone());
        mLocation.setText(user.getLocation());
        mSpecialization.setText(user.getSpecialization());
        mLicense.setText(user.getLicense());
        mEducation.setText(user.getEducation());
    }

    public void getDoctorViews(){
        mName = findViewById(R.id.profile_name);
        mEmail = findViewById(R.id.profile_email);
        mPhone = findViewById(R.id.profile_phone);
        mLocation = findViewById(R.id.profile_location);
        mLicense = findViewById(R.id.profile_license);
        mSpecialization = findViewById(R.id.profile_specialization);
        mEducation = findViewById(R.id.profile_education);
    }

    public void getUserViews() {
        return;
    }
}
