package com.raymondliang.myapplication;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Connection;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import com.opentok.android.SubscriberKit;
import com.raymondliang.myapplication.Data.User;
import com.raymondliang.myapplication.utils.WebServiceCoordinator;

import androidx.annotation.NonNull;
import android.Manifest;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoCallActivity extends AppCompatActivity
        implements  Session.SessionListener,
        PublisherKit.PublisherListener,
        SubscriberKit.SubscriberListener,
        Session.SignalListener,
        WebServiceCoordinator.Listener {
    public static final String SESSION_ID = "session_id";

    public VideoCallActivity() {
        super(R.layout.activity_videocall);
    }

    private static final String TAG = "VideoCallActivity";
    private static final String LOG_TAG = VideoCallActivity.class.getSimpleName();
    private static final int RC_VIDEO_APP_PERM = 124;
    private Session mSession;
    private Publisher mPublisher;
    private Subscriber mSubscriber;
    private FrameLayout mPublisherViewContainer;
    private FrameLayout mSubscriberViewContainer;
    private FloatingActionButton cameraflipfab;
    private FloatingActionButton videotogglefab;
    private FloatingActionButton mictogglefab;
    private FloatingActionButton callendfab;
    boolean videoflag = true;
    boolean micflag = true;
    private Chronometer calltimer;
    private User currUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize view layouts
        mPublisherViewContainer = findViewById(R.id.publisher_container);
        mSubscriberViewContainer = findViewById(R.id.subscriber_container);
        cameraflipfab = findViewById(R.id.switch_camera_action_fab);
        videotogglefab = findViewById(R.id.local_video_action_fab);
        mictogglefab = findViewById(R.id.mute_action_fab);
        callendfab = findViewById(R.id.end_call_fab);
        calltimer = findViewById(R.id.calltimetracker);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            retrieveUserInfo(uid);
        }
        requestPermissions();


        /* buttons on bottom right of page*/
        callendfab.setOnClickListener(v -> {
            mSession.disconnect();
            finish();
        });

        cameraflipfab.setOnClickListener(v -> mPublisher.cycleCamera());

       videotogglefab.setOnClickListener(v -> {

           if (videoflag){
               videotogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_videocam_white_24dp));
               mPublisher.setPublishVideo(true);
               videoflag = false;

           } else {
               videotogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_videocam_off_black_24dp));
               mPublisher.setPublishVideo(false);
               videoflag = true;
           }
       });

        mictogglefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (micflag){
                    mictogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mic_white_24dp));
                    mPublisher.setPublishAudio(true);
                    micflag = false;

                } else {
                    mictogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mic_off_black_24dp));
                    mPublisher.setPublishAudio(false);
                    micflag = true;
                }
            }
        });
    }

    private void retrieveUserInfo(final String userId) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        // Get user value
                        currUser = dataSnapshot.getValue(User.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                    }
                });
    }

    /* Activity lifecycle methods */

    @Override
    protected void onPause() {

        Log.d(LOG_TAG, "onPause");

        super.onPause();

        if (mSession != null) {
            mSession.onPause();
        }

    }

    @Override
    protected void onResume() {

        Log.d(LOG_TAG, "onResume");

        super.onResume();

        if (mSession != null) {
            mSession.onResume();
        }
    }

    @Override
    protected void onDestroy(){
        Log.d(LOG_TAG, "onDestroy");

        super.onDestroy();
    }

    // request permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        final Intent i = getIntent();
        final String sessionId = i.getExtras().getString(SESSION_ID);
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize and connect to the session
            fetchSessionConnectionData(sessionId);

        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    public void fetchSessionConnectionData(final String sessionId) {
        RequestQueue reqQueue = Volley.newRequestQueue(this);
        reqQueue.add(new JsonObjectRequest(Request.Method.GET,
                "https://unlvtelemedicine.herokuapp.com/" + sessionId,
                null, response -> {
                    try {
                        String API_KEY = response.getString("apiKey");
                        String SESSION_ID = response.getString("sessionId");
                        String TOKEN = response.getString("token");

                        Log.i(LOG_TAG, "API_KEY: " + API_KEY);
                        Log.i(LOG_TAG, "SESSION_ID: " + SESSION_ID);
                        Log.i(LOG_TAG, "TOKEN: " + TOKEN);

                        initializeSession(API_KEY, SESSION_ID, TOKEN);

                    } catch (JSONException error) {
                        Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
            }
        }));
    }

    private void initializeSession(String apiKey, String sessionId, String token) {

        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.setSignalListener(this);
        mSession.connect(token);
    }

    // SessionListener methods

    @Override
    public void onConnected(Session session) {
        Log.d(LOG_TAG, "Session Connected");

        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).name(currUser.getName()).build();
        mPublisher.setPublisherListener(this);
        // set publisher video style to fill view
        mPublisher.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE,
                BaseVideoRenderer.STYLE_VIDEO_FILL);
        mPublisherViewContainer.addView(mPublisher.getView());
        if (mPublisher.getView() instanceof GLSurfaceView) {
            ((GLSurfaceView) mPublisher.getView()).setZOrderOnTop(true);
        }
        mSession.publish(mPublisher);

    }

    @Override
    public void onDisconnected(Session session) {
        Log.d(LOG_TAG, "onDisconnected: Disconnected from session: "+session.getSessionId());
    }

    @Override
    public void onStreamReceived(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Received");

        if (mSubscriber == null) {
            mSubscriber = new Subscriber.Builder(this, stream).build();
            mSubscriber.getRenderer().setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL);
            mSubscriber.setSubscriberListener(this);
            mSession.subscribe(mSubscriber);
            mSubscriberViewContainer.addView(mSubscriber.getView());
            calltimer.setBase(SystemClock.elapsedRealtime());
            calltimer.start();
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
            calltimer.stop();
            long timeEplapsed = SystemClock.elapsedRealtime() - calltimer.getBase();
            Toast.makeText(this, "Elapsed Call Time: " + convertTime(timeEplapsed), Toast.LENGTH_LONG).show();
        }
        //finish();
    }

    @Override
    public void onError(Session session, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: "+ opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() + " - "+opentokError.getMessage() + " in session: "+ session.getSessionId());

        showOpenTokError(opentokError);
    }

    // PublisherListener methods

    @Override
    public void onStreamCreated(PublisherKit publisherKit, Stream stream) {
        Log.d(LOG_TAG, "onStreamCreated: Publisher Stream Created. Own stream "+stream.getStreamId());
        Log.d(TAG, "Publisher name: " + mPublisher.getName());
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.d(LOG_TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream "+stream.getStreamId());
        //mPublisherViewContainer.removeAllViews();
    }

    @Override
    public void onError(PublisherKit publisherKit, OpentokError opentokError) {
        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());
        showOpenTokError(opentokError);
    }

    /*
    Subscriber methods
     */

    @Override
    public void onConnected(SubscriberKit subscriberKit) {

        Log.d(LOG_TAG, "onConnected: Subscriber connected. Stream: "+subscriberKit.getStream().getStreamId());
        Toast.makeText(this, "Connected to " + mSubscriber.getStream().getName() + "'s call.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected(SubscriberKit subscriberKit) {
        Log.d(LOG_TAG, "onDisconnected: Subscriber disconnected. Stream: "+subscriberKit.getStream().getStreamId());
    }

    @Override
    public void onError(SubscriberKit subscriberKit, OpentokError opentokError) {

        Log.e(LOG_TAG, "onError: "+opentokError.getErrorDomain() + " : " +
                opentokError.getErrorCode() +  " - "+opentokError.getMessage());

        showOpenTokError(opentokError);
    }

    private void showOpenTokError(OpentokError opentokError) {

        Toast.makeText(this, opentokError.getErrorDomain().name() +": " +opentokError.getMessage() + " Please, see the logcat.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onSignalReceived(Session session, String type, String data, Connection connection) {
        String myConnectionId = session.getConnection().getConnectionId();
        if (connection != null && connection.getConnectionId().equals(myConnectionId)) {
            Toast toast = Toast.makeText(this, data, Toast.LENGTH_LONG);
            toast.show();
        }
    }

    private String convertTime(long millis){
        String hms = String.format(Locale.getDefault(), "%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
        return hms;
    }

    private void showConfigError(String alertTitle, final String errorMessage) {
        Log.e(LOG_TAG, "Error " + alertTitle + ": " + errorMessage);
        new AlertDialog.Builder(this)
                .setTitle(alertTitle)
                .setMessage(errorMessage)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        VideoCallActivity.this.finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onSessionConnectionDataReady(String apiKey, String sessionId, String token) {

        Log.d(LOG_TAG, "ApiKey: "+apiKey + " SessionId: "+ sessionId + " Token: "+token);
        initializeSession(apiKey, sessionId, token);
    }

    @Override
    public void onWebServiceCoordinatorError(Exception error) {

        Log.e(LOG_TAG, "Web Service error: " + error.getMessage());
        Toast.makeText(this, "Web Service error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        finish();

    }
}
