package com.raymondliang.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserListActivity extends AppCompatActivity {

    private static final String TAG = "UserListActivity";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<UserItem> userList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        mSwipeContainer = findViewById(R.id.swipeContainer);
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userList.clear();
                mRecyclerView.removeAllViewsInLayout();
                updateRecyclerView();
                mSwipeContainer.setRefreshing(false);
            }
        });

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(UserListActivity.this);
        updateRecyclerView();
        mAdapter = new UserAdapter(userList);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void updateRecyclerView(){

        final DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        final String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        mDatabase.child("Users").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = null;
                        // Get user value
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            user = ds.getValue(User.class);
                            String currEmail = user.getUserEmail();
                            if(!currEmail.equals(userEmail)) {
                                if (!user.getAvailability()) {
                                    userList.add(new UserItem(R.drawable.ic_android, user.getUserName(),
                                            R.drawable.button_offline, "Offline"));
                                } else {
                                    userList.add(new UserItem(R.drawable.ic_android, user.getUserName(),
                                            R.drawable.button_online, "Online"));
                                }
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

}
