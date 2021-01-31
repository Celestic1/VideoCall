package com.raymondliang.myapplication;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class WaitingRoomActivity extends AppCompatActivity {

    private static final String TAG = "WaitingRoomActivity";
    private Button callButton;
    private DatabaseReference mDatabase;
    private String ReceiverName;
    private String senderUID, receiverUID;
    private String receiverToken;
    private String senderName;
    private FirebaseAuth mAuth;
    private Map<String, Object> map = new HashMap<>();
    private Map<String, Object> receiverSS = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        callButton = findViewById(R.id.call_button);

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                ReceiverName = getIntent().getExtras().getString("displayedName");
                senderUID = mAuth.getCurrentUser().getUid();
                mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds : dataSnapshot.getChildren()){
                            Map<String, Object> ss= (Map<String, Object>) ds.getValue();
                            String currUID = (String) ss.get("uid");
                            String currName = (String) ss.get("name");

                            if(currName.equals(ReceiverName)){
                                receiverSS.putAll(ss);
                            }

                            if(currUID.equals(senderUID)){
                                senderName  = currName;
                            }

                            if(senderName != null && receiverSS.size() != 0) {
                                if ((receiverSS.get("name")).equals(ReceiverName)) {
                                    receiverUID = (String) receiverSS.get("uid");
                                    receiverToken = (String) receiverSS.get("device_token");
                                    map.put("from", senderUID);
                                    map.put("Receiver_Device_Token", receiverToken);
                                    map.put("from_name", senderName);
                                    mDatabase.child("Notifications").child(receiverUID).push().setValue(map);
                                    map.clear();
                                    startActivity(new Intent(WaitingRoomActivity.this, VideoCallActivity.class));
                                    finish();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
