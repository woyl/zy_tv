package com.zhongyou.meettvapplicaion.business;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.event.ResolutionChangeEvent;
import com.zhongyou.meettvapplicaion.utils.RxBus;

import io.agora.openlive.model.ConstantApp;

/**
 * Created by whatisjava on 17-9-5.
 */

public class ResolutionActivity extends BasisActivity {

    private SharedPreferences pref;
    private RadioGroup radioGroup;

    @Override
    public String getStatisticsTag() {
        return "分辨率设置";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resolution);

        pref = PreferenceManager.getDefaultSharedPreferences(this);
        int prefIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX);

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        ((RadioButton)radioGroup.getChildAt(prefIndex - 2)).setChecked(true);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                SharedPreferences.Editor editor = pref.edit();
                switch (checkedId) {
                    case R.id.resolution_1:
                        editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, 2);
                        editor.apply();
                        break;
                    case R.id.resolution_2:
                        editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, 3);
                        editor.apply();
                        break;
                    case R.id.resolution_3:
                        editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, 4);
                        editor.apply();
                        break;
                }
                ResolutionChangeEvent resolutionChangeEvent = new ResolutionChangeEvent();
                resolutionChangeEvent.setResolution(pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_PROFILE_IDX, ConstantApp.DEFAULT_PROFILE_IDX));
                RxBus.sendMessage(resolutionChangeEvent);
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
