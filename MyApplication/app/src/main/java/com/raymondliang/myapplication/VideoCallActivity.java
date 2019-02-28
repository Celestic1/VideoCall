package com.raymondliang.myapplication;

import android.content.Intent;
import android.media.AudioManager;
import android.opengl.GLSurfaceView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;

import com.opentok.android.BaseVideoRenderer;
import com.opentok.android.Session;
import com.opentok.android.Stream;
import com.opentok.android.Publisher;
import com.opentok.android.PublisherKit;
import com.opentok.android.Subscriber;
import com.opentok.android.OpentokError;
import com.opentok.android.SubscriberKit;

import android.support.annotation.NonNull;
import android.Manifest;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import android.view.View;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.Toast;

public class VideoCallActivity extends AppCompatActivity implements  Session.SessionListener, PublisherKit.PublisherListener, SubscriberKit.SubscriberListener{

    private static String API_KEY = "46265502";
    private static String SESSION_ID = "1_MX40NjI2NTUwMn5-MTU1MDIxMjkzNTQ2NH4zd3R1ZTh1ZkV2dVRnZmF0Z0sxVEZoTVF-fg";
    private static String TOKEN = "T1==cGFydG5lcl9pZD00NjI2NTUwMiZzaWc9ZmY0YTIwYjhhM2FmMzE5ZTg3YjQ2YTdjODViMzE3YzAxMzhkNDFiNDpzZXNzaW9uX2lkPTFfTVg0ME5qSTJOVFV3TW41LU1UVTFNREl4TWprek5UUTJOSDR6ZDNSMVpUaDFaa1YyZFZSblptRjBaMHN4VkVab1RWRi1mZyZjcmVhdGVfdGltZT0xNTUwMjE5NzYxJm5vbmNlPTAuMDg2ODA1OTk5Mzg5Nzg4OTUmcm9sZT1wdWJsaXNoZXImZXhwaXJlX3RpbWU9MTU1MDgyNDYzNSZpbml0aWFsX2xheW91dF9jbGFzc19saXN0PQ==";

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
    boolean videoflag = true;
    boolean micflag = true;
    private Chronometer calltimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall);

        // initialize view layouts
        mPublisherViewContainer = findViewById(R.id.publisher_container);
        mSubscriberViewContainer = findViewById(R.id.subscriber_container);
        cameraflipfab = findViewById(R.id.switch_camera_action_fab);
        videotogglefab = findViewById(R.id.local_video_action_fab);
        mictogglefab = findViewById(R.id.mute_action_fab);
        calltimer = findViewById(R.id.calltimetracker);

        requestPermissions();

        // Enable changing the volume using the up/down keys during a conversation
        //setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);

        cameraflipfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPublisher.cycleCamera();
            }
        });

       videotogglefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(videoflag){
                    videotogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_videocam_white_24dp));
                    mPublisher.setPublishVideo(true);
                    videoflag = false;

                }else if(!videoflag){
                    videotogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_videocam_off_black_24dp));
                    mPublisher.setPublishVideo(false);
                    videoflag = true;
                }
            }
        });

        mictogglefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(micflag){
                    mictogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mic_white_24dp));
                    mPublisher.setPublishAudio(true);
                    micflag = false;

                }else if(!micflag){
                    mictogglefab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_mic_off_black_24dp));
                    mPublisher.setPublishAudio(false);
                    micflag = true;
                }
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

    // request permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(RC_VIDEO_APP_PERM)
    private void requestPermissions() {
        String[] perms = { Manifest.permission.INTERNET, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
        if (EasyPermissions.hasPermissions(this, perms)) {
            // initialize and connect to the session
            initializeSession(API_KEY, SESSION_ID, TOKEN);

        } else {
            EasyPermissions.requestPermissions(this, "This app needs access to your camera and mic to make video calls", RC_VIDEO_APP_PERM, perms);
        }
    }

    private void initializeSession(String apiKey, String sessionId, String token) {

        mSession = new Session.Builder(this, apiKey, sessionId).build();
        mSession.setSessionListener(this);
        mSession.connect(token);
    }

    // SessionListener methods

    @Override
    public void onConnected(Session session) {
        Log.d(LOG_TAG, "Session Connected");

        // initialize Publisher and set this object to listen to Publisher events
        mPublisher = new Publisher.Builder(this).build();
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
            calltimer.start();
        }
    }

    @Override
    public void onStreamDropped(Session session, Stream stream) {
        Log.i(LOG_TAG, "Stream Dropped");

        if (mSubscriber != null) {
            mSubscriber = null;
            mSubscriberViewContainer.removeAllViews();
        }
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
    }

    @Override
    public void onStreamDestroyed(PublisherKit publisherKit, Stream stream) {
        Log.d(LOG_TAG, "onStreamDestroyed: Publisher Stream Destroyed. Own stream "+stream.getStreamId());
        mPublisher = null;
        mPublisherViewContainer.removeAllViews();
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


}
