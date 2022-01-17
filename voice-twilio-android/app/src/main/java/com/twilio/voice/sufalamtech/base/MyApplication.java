package com.twilio.voice.sufalamtech.base;

import android.app.Application;

import com.google.firebase.messaging.FirebaseMessaging;
import com.twilio.voice.LogLevel;
import com.twilio.voice.Voice;
import com.twilio.voice.sufalamtech.PrefHelper;

public class MyApplication extends Application {


    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Voice.setLogLevel(LogLevel.DEBUG);

        refreshFCMToken();
    }

    private void refreshFCMToken() {

        if (!PrefHelper.getInstance().getString(PrefHelper.FCM_TOKEN, "").isEmpty()) {
            return;
        }

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(token -> {
            PrefHelper.getInstance().setString(PrefHelper.FCM_TOKEN, token);
        });
    }

    public static synchronized MyApplication getInstance() {
        return instance;
    }
}
