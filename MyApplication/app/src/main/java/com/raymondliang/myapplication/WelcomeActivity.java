package com.raymondliang.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    private TextView title, profile, statusText;
    private Button logout, docList, pharmLoc, status;
    private String uid, s;
    private DatabaseReference mDatabase;
    private User currUser;
    private Map<String, Object> map = new HashMap<>();
    private Boolean online = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        title = findViewById(R.id.tv_welcome);
        profile = findViewById(R.id.tv_profile);
        logout = findViewById(R.id.logout_button);
        docList = findViewById(R.id.viewAvailable_button);
        pharmLoc = findViewById(R.id.pharmacy_location_button);
        status = findViewById(R.id.status_button);
        statusText = findViewById(R.id.status_text);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            uid = user.getUid();
            retrieveUserInfo(uid);
        }

    }

    private void retrieveUserInfo(final String userId) {
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user value
                        currUser = dataSnapshot.getValue(User.class);
                        String msg = "Welcome " + currUser.getUserName();
                        title.setText(msg);

                        if(!currUser.getAvailability()){
                            s = "Currently Offline";
                            statusText.setText(s);
                            statusText.setTextColor(Color.RED);
                        } else {
                            s = "Currently Online";
                            statusText.setText(s);
                            statusText.setTextColor(Color.GREEN);
                        }

                        title.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.VISIBLE);
                        logout.setVisibility(View.VISIBLE);
                        docList.setVisibility(View.VISIBLE);
                        //pharmLoc.setVisibility(View.VISIBLE);
                        status.setVisibility(View.VISIBLE);
                        statusText.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    public void profileSettings(View view) {
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("User info", currUser);
        startActivity(i);
    }

    public void logout(View view) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void viewUserList(View view) {
        Intent i = new Intent(this, UserListActivity.class);
        startActivity(i);
    }


    public void findPharmacy(View view) {
        Intent i = new Intent(this, PharmacyLocatorActivity.class);
        i.putExtra("User info", currUser);
        startActivity(i);
    }

    public void changeStatus(View view) {
        Log.d(TAG, "status button clicked");

        if(!online) {
            online = true;
            map.put("availability", online);
            mDatabase.child("Users").child(uid).updateChildren(map);
            mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("availability").getValue() == online) {
                                s = "Currently Online";
                                statusText.setText(s);
                                statusText.setTextColor(Color.GREEN);
                                Toast.makeText(WelcomeActivity.this, "Online status changed.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        } else {
            online = false;
            map.put("availability", online);
            mDatabase.child("Users").child(uid).updateChildren(map);
            mDatabase.child("Users").child(uid).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.child("availability").getValue() == online) {
                                s = "Currently Offline";
                                statusText.setText(s);
                                statusText.setTextColor(Color.RED);
                                Toast.makeText(WelcomeActivity.this, "Online status changed.", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        }
                    });
        }
    }
}


