package com.raymondliang.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToLogin(View view) {
        Intent LoginIntent = new Intent(this, LoginActivity.class);
        startActivity(LoginIntent);
    }

    public void goToSignUp(View view) {
        Intent registerIntent = new Intent(this, RegistrationActivity.class);
        startActivity(registerIntent);
    }

    public void test(View view) {
        Intent l = new Intent(this, VideoCallActivity.class);
        startActivity(l);
    }
}
