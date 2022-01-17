package com.twilio.voice.sufalamtech;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.twilio.voice.sufalamtech.databinding.ActivitySplashBinding;
import com.twilio.voice.sufalamtech.model.AccessToken;

import java.util.Date;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();
    }

    private void initViews() {

        AccessToken accessToken = AccessToken.getInstance();
        //Here we check if user is already registered or not
        //Also the access token generated from twilio is expired or not
        //If expired we will need re-register user
        if (accessToken == null || accessToken.getTokenExpiredTime() < new Date().getTime()) {
            startActivity(new Intent(this, RegistrationActivity.class));
        } else {
            startActivity(new Intent(this, VoiceActivity.class));
        }
        finish();
    }
}