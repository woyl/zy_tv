package io.agora.openlive.ui;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Agora;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;

import io.agora.openlive.model.ConstantApp;
import io.agora.rtc.Constants;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public final String FTAG = Logger.lifecycle;

    private String channelId, callInfo, deviceInfo, photo, name, shopPhoto;
    private Agora agora;
    private int shopOwnerResolution;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            forwardToLiveRoom(Constants.CLIENT_ROLE_BROADCASTER);
        }
    };

    private AudioManager mAudioManager;

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d(FTAG + TAG, "onResume");
        ZYAgent.onPageStart(this, "视频通话");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Logger.d(FTAG + TAG, "onResume");
        ZYAgent.onPageEnd(this, "视频通话");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_openlive);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);

    }

    private AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:
                    Log.d(TAG, "AUDIOFOCUS_GAIN [" + this.hashCode() + "]");

                    break;
                case AudioManager.AUDIOFOCUS_LOSS:

                    Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]");
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

                    Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK [" + this.hashCode() + "]");
                    break;
            }
        }
    };

    @Override
    protected void initUIandEvent() {
        channelId = getIntent().getStringExtra("channelId");
        callInfo = getIntent().getStringExtra("callInfo");
        deviceInfo = getIntent().getStringExtra("deviceInfo");
        agora = getIntent().getParcelableExtra("agora");
        shopOwnerResolution = getIntent().getIntExtra("shopOwnerResolution", 0);
        photo = getIntent().getStringExtra("photo");
        name = getIntent().getStringExtra("name");
        shopPhoto = getIntent().getStringExtra("shopPhoto");
        setAppId(agora.getAppID());

        EditText textRoomName = (EditText) findViewById(R.id.room_name);
        textRoomName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.toString().isEmpty();
                findViewById(R.id.button_join).setEnabled(!isEmpty);
            }
        });

        textRoomName.setText(channelId);

        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    protected void deInitUIandEvent() {
    }

    public void forwardToLiveRoom(int cRole) {
        final EditText v_room = (EditText) findViewById(R.id.room_name);
        String room = v_room.getText().toString();

        Intent i = new Intent(MainActivity.this, LiveRoomActivity.class);
        i.putExtra(ConstantApp.ACTION_KEY_CROLE, cRole);
        i.putExtra(ConstantApp.ACTION_KEY_ROOM_NAME, room);
        i.putExtra("callInfo", callInfo);
        i.putExtra("deviceInfo", deviceInfo);
        i.putExtra("agora", agora);
        i.putExtra("shopOwnerResolution", shopOwnerResolution);
        i.putExtra("photo", photo);
        i.putExtra("name", name);
        i.putExtra("shopPhoto", shopPhoto);
        startActivity(i);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener);
    }
}
