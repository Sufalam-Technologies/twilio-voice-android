package com.twilio.voice.sufalamtech.model;

import com.google.gson.Gson;
import com.twilio.voice.sufalamtech.PrefHelper;

public class AccessToken {
    private String token = "";
    private long tokenGenerateTime = 0;
    private long tokenExpiredTime = 0;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTokenGenerateTime() {
        return tokenGenerateTime;
    }

    public void setTokenGenerateTime(long tokenGenerateTime) {
        this.tokenGenerateTime = tokenGenerateTime;
    }

    public long getTokenExpiredTime() {
        return tokenExpiredTime;
    }

    public void setTokenExpiredTime(long tokenExpiredTime) {
        this.tokenExpiredTime = tokenExpiredTime;
    }

    public static AccessToken getInstance() {
        return new Gson().fromJson(PrefHelper.getInstance().getString(PrefHelper.ACCESS_TOKEN, ""), AccessToken.class);
    }

    public static void save(AccessToken accessToken) {
        PrefHelper.getInstance().setString(PrefHelper.ACCESS_TOKEN, new Gson().toJson(accessToken));
    }

    public static void save(String jsonAccessToken) {
        PrefHelper.getInstance().setString(PrefHelper.ACCESS_TOKEN, jsonAccessToken);
    }
}
