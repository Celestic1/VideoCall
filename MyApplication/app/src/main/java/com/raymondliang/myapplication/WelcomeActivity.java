package com.raymondliang.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    private TextView title, profile;
    private Button logout, docList, pharmLoc;
    private DatabaseReference mDatabase;
    private User currUser;

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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
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

                        title.setVisibility(View.VISIBLE);
                        profile.setVisibility(View.VISIBLE);
                        logout.setVisibility(View.VISIBLE);
                        docList.setVisibility(View.VISIBLE);
                        pharmLoc.setVisibility(View.VISIBLE);

                        // [START_EXCLUDE]
                        if (currUser == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(WelcomeActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                            finish();
                        }
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

    public void viewContactList(View view) {
    }


    public void findPharmacy(View view) {
    }
}


