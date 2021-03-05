package com.raymondliang.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raymondliang.myapplication.Data.Doctor;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {
    private static final String TAG = "UserListActivity";

    public UserListActivity() {
        super(R.layout.activity_user_list);
    }

    private RecyclerView recyclerView;
    private UserAdapter adapter;
    private ArrayList<DoctorItem> doctorList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SwipeRefreshLayout mSwipeContainer = findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(() -> {
            doctorList.clear();
            recyclerView.removeAllViewsInLayout();
            updateRecyclerView();
            //mSwipeContainer.setRefreshing(false);
        });

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        updateRecyclerView();
        adapter = new UserAdapter(doctorList, doctorItem -> {
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent i = new Intent(this, VideoCallActivity.class);
                        // TODO: Change this to retrieve session per the doctor remotely from FireBase
                        i.putExtra(VideoCallActivity.SESSION_ID, "session");
                        startActivity(i);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            if (doctorItem.availability) {
                builder.setMessage("Start a call?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            } else {
                builder.setMessage("Doctor is offline, cannot call").setPositiveButton("OK", null).show();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        recyclerView.setAdapter(adapter);
    }

    private void updateRecyclerView(){

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        mDatabase.child("Doctors").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Doctor doctor = null;
                        // Get user value
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            doctor = ds.getValue(Doctor.class);
                            String currEmail = doctor.getEmail();
                            if(!currEmail.equals(userEmail)) {
                                doctorList.add(new DoctorItem(doctor.getName(), doctor.getAvailability(), null, R.drawable.ic_android));
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }
}
