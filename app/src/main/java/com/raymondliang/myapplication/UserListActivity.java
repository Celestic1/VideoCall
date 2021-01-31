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

public class UserListActivity extends AppCompatActivity implements UserAdapter.UserAdapterOnClickHandler {

    private static final String TAG = "UserListActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<DoctorItem> doctorList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mSwipeContainer = findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(() -> {
            doctorList.clear();
            mRecyclerView.removeAllViewsInLayout();
            updateRecyclerView();
            //mSwipeContainer.setRefreshing(false);
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        updateRecyclerView();
        mAdapter = new UserAdapter(doctorList, this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        mRecyclerView.setAdapter(mAdapter);
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
                        mAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    @Override
    public void onClick(DoctorItem doctorItem) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent i = new Intent(this, VideoCallActivity.class);
                    startActivity(i);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Start a call?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
