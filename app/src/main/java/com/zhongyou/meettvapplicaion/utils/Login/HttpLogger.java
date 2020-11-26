package com.zhongyou.meettvapplicaion.utils.Login;

import android.util.Log;

import okhttp3.logging.HttpLoggingInterceptor;

public class HttpLogger implements HttpLoggingInterceptor.Logger {
    @Override
    public void log(String message) {
        Log.e("lt_cj",message);
    }
}
