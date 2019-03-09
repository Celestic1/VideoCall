package com.raymondliang.myapplication;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText regEmail, regPassword, regPhone, regName, regAddress;
    private String name;
    private String email;
    private String phone;
    private String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        regEmail = findViewById(R.id.reg_email);
        regPassword = findViewById(R.id.reg_password);
        regName = findViewById(R.id.reg_name);
        regPhone = findViewById(R.id.reg_phone);
        regAddress = findViewById(R.id.reg_address);
    }

    public void signup(View view) {
        mAuth = FirebaseAuth.getInstance();
        String email = regEmail.getText().toString();
        String password = regPassword.getText().toString();

        if(!validate()){
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            postUserData();
                            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RegistrationActivity.this, "User Authentication Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void postUserData(){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        User user = new User(name, email, phone, address, false);

        mDatabase.child("Users").child(mAuth.getUid()).setValue(user);
    }

    private boolean validate(){

        boolean valid = true;

        email = regEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            regEmail.setError("Required.");
            valid = false;
        } else {
            regEmail.setError(null);
        }

        String password = regPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            regPassword.setError("Required.");
            valid = false;
        } else {
            regPassword.setError(null);
        }

        name = regName.getText().toString();
        if (TextUtils.isEmpty(name)) {
            regName.setError("Required.");
            valid = false;
        } else {
            regName.setError(null);
        }

        phone = regPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            regPhone.setError("Required.");
            valid = false;
        } else {
            regPhone.setError(null);
        }

        address = regAddress.getText().toString();
        if (TextUtils.isEmpty(address)) {
            regAddress.setError("Required.");
            valid = false;
        } else {
            regAddress.setError(null);
        }

        return valid;
    }

    public void toLogin(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }
}
