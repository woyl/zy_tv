package com.zhongyou.meettvapplicaion.receiver;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.zhongyou.meettvapplicaion.event.HangDownEvent;
import com.zhongyou.meettvapplicaion.event.HangOnEvent;
import com.zhongyou.meettvapplicaion.utils.RxBus;


/**
 * Created by whatisjava on 17-8-7.
 */

public class PhoneReceiver extends BroadcastReceiver {

    private boolean incomingFlag = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        //拨打电话
        if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
            incomingFlag = false;
            final String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            Log.d("PhoneReceiver", "phoneNum: " + phoneNum);
        } else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    final PhoneStateListener listener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                //电话响铃
                case TelephonyManager.CALL_STATE_RINGING:
                    incomingFlag = true;
                    Log.i("PhoneReceiver", "CALL IN RINGING :" + incomingNumber);
                    break;
                //电话接听
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    if (incomingFlag) {
                        Log.i("PhoneReceiver", "CALL IN ACCEPT :" + incomingNumber);
                        RxBus.sendMessage(new HangOnEvent());
                    }
                    break;
                //电话挂机
                case TelephonyManager.CALL_STATE_IDLE:
                    if (incomingFlag) {
                        Log.i("PhoneReceiver", "CALL IDLE");
                        RxBus.sendMessage(new HangDownEvent());
                        incomingFlag = false;
                    }
                    break;
            }
        }
    };

}
