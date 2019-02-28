package com.raymondliang.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private TextView name, email, phone, address;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        getViews();
        updateUserProfile();
    }

    private void updateUserProfile(){

        // retrieves object passed through intent
        Intent i = getIntent();
        User user = (User)i.getSerializableExtra("User info");

        // updates fields
        name.setText(user.getUserName());
        email.setText(user.getUserEmail());
        phone.setText(user.getUserPhone());
        address.setText(user.getUserAddress());

        // sets field visible to remove text change delays
        name.setVisibility(View.VISIBLE);
        email.setVisibility(View.VISIBLE);
        phone.setVisibility(View.VISIBLE);
        address.setVisibility(View.VISIBLE);

    }

    private void getViews(){
        name = findViewById(R.id.profile_name);
        email = findViewById(R.id.profile_email);
        phone = findViewById(R.id.profile_phone);
        address = findViewById(R.id.profile_address);
    }
}
