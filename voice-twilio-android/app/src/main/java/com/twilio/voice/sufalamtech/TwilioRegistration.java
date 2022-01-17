package com.twilio.voice.sufalamtech;

import android.util.Log;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;
import com.twilio.voice.sufalamtech.base.MyApplication;
import com.twilio.voice.sufalamtech.model.AccessToken;

import java.util.Date;

public class TwilioRegistration {

    private static final TwilioRegistration instance = new TwilioRegistration();

    //    private String url = "http://172.104.173.184:3000/accessToken?identity=";
    private String url = "https://22c3-2409-4041-2d0d-aabd-9da-3fdd-53ac-4826.ngrok.io/accessToken?identity=";

    public static TwilioRegistration getInstance() {
        return instance;
    }

    private void registerFCMToken(TwilioFCMRegisterListener listener) {

        Voice.register(AccessToken.getInstance().getToken(), Voice.RegistrationChannel.FCM,
                PrefHelper.getInstance().getString(PrefHelper.FCM_TOKEN, ""),
                new RegistrationListener() {
                    @Override
                    public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
                        PrefHelper.getInstance().setBoolean(PrefHelper.FCM_TOKEN_REGISTER, true);
                        listener.onSuccessToken(fcmToken);
                    }

                    @Override
                    public void onError(@NonNull RegistrationException registrationException, @NonNull String accessToken, @NonNull String fcmToken) {
                        Log.e("registrationError", registrationException.getMessage());
                        listener.onFailureToken(registrationException.getMessage());
                    }
                }
        );
    }

    public void generateAccessToken(String identity, AccessTokenListener listener) {

        AccessToken accessToken = AccessToken.getInstance();

        if (accessToken == null || accessToken.getTokenExpiredTime() < new Date().getTime() || !PrefHelper.getInstance().getBoolean(PrefHelper.FCM_TOKEN_REGISTER, false)) {
            PrefHelper.getInstance().setBoolean(PrefHelper.FCM_TOKEN_REGISTER, false);
            url = url + identity;
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, response -> {
                        AccessToken.save(response.toString());
                        registerFCMToken(new TwilioFCMRegisterListener() {
                            @Override
                            public void onSuccessToken(String fcmToken) {
                                listener.onSuccessToken(AccessToken.getInstance().getToken(), fcmToken);
                            }

                            @Override
                            public void onFailureToken(String error) {
                                listener.onFailureToken(error);
                            }
                        });
                    }, error -> {
//                        Log.e("tokenError", error.getMessage());
                        listener.onFailureToken(error.getMessage());
                    });

            // Access the RequestQueue through your singleton class.
            MySingleton.getInstance(MyApplication.getInstance()).addToRequestQueue(jsonObjectRequest);
        } else {
            listener.onSuccessToken(accessToken.getToken(), PrefHelper.getInstance().getString(PrefHelper.FCM_TOKEN, ""));
        }
    }

    public interface AccessTokenListener {

        void onSuccessToken(String accessToken, String fcmToken);

        void onFailureToken(String error);
    }

    public interface TwilioFCMRegisterListener {

        void onSuccessToken(String fcmToken);

        void onFailureToken(String error);
    }
}
