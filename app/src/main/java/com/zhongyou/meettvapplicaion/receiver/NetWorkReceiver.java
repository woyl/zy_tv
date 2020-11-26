package com.zhongyou.meettvapplicaion.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zhongyou.meettvapplicaion.event.HangDownEvent;
import com.zhongyou.meettvapplicaion.event.HangOnEvent;
import com.zhongyou.meettvapplicaion.utils.RxBus;


public class NetWorkReceiver extends BroadcastReceiver {

    private static final String TAG = NetWorkReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                RxBus.sendMessage(new HangDownEvent());
                Log.d(TAG, "网络从不可用状态到可用状态");
            } else {
                RxBus.sendMessage(new HangOnEvent());
                Log.d(TAG, "网络从可用状态到不可用状态");
            }
        }
    }

}