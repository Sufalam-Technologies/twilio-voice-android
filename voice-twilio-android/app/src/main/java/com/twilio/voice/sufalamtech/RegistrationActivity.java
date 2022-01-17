package com.twilio.voice.sufalamtech;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.twilio.voice.sufalamtech.databinding.ActivityRegistrationBinding;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityRegistrationBinding binding;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initViews();
    }

    private void initViews() {
        binding.btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnRegister) {
            register();
        }
    }

    /**
     * Check validation and authenticate and register user to twilio server
     */
    private void register() {
        if (TextUtils.isEmpty(Objects.requireNonNull(binding.etName.getText()).toString().trim())) {
            showMsg("Please enter valid name");
        } else if (!isOnline()) {
            showMsg("Check your internet connection");
        } else {
            registerForCallInvites();
        }
    }

    /*
     * Register your FCM token with Twilio to receive incoming call invites
     */
    private void registerForCallInvites() {
        showProgress();
        TwilioRegistration.getInstance().generateAccessToken(Objects.requireNonNull(binding.etName.getText()).toString().trim(), new TwilioRegistration.AccessTokenListener() {
            @Override
            public void onSuccessToken(String accessToken, String fcmToken) {
                dismissProgress();
                PrefHelper.getInstance().getString(PrefHelper.USER_IDENTITY, binding.etName.getText().toString().trim());
                showMsg("Successfully registered.");
                startActivity(new Intent(RegistrationActivity.this, VoiceActivity.class));
                finish();
            }

            @Override
            public void onFailureToken(String error) {
                dismissProgress();
                showMsg(error);
            }
        });
    }

    /**
     * Show loading dialog
     */
    private void showProgress() {
        try {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Authentication");
            progressDialog.setMessage("Please wait a moment...");
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Dismiss the progress dialog
     */
    private void dismissProgress() {
        try {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return the boolean value, If app device has active internet connection,
     * its return true else false
     */
    public boolean isOnline() {

        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            assert connectivityManager != null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET);
                }
            } else {

                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Show messages in snackbar
     */
    private void showMsg(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).setAction("OK", v -> {

        }).show();
    }
}