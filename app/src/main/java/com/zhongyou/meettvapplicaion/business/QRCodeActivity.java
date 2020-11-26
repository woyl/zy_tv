package com.zhongyou.meettvapplicaion.business;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.event.EditUserinfoEvent;
import com.zhongyou.meettvapplicaion.net.ApiClient;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.MethodUtils;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;

import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by wufan on 2017/8/25.
 */

public class QRCodeActivity extends BasisActivity {


    private TextView tipsText;
    private ImageView qrcodeImage;
    private Subscription subscription;

    @Override
    public String getStatisticsTag() {
        return "编辑资料弹窗";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_wechat);
        subscription = RxBus.handleMessage(new Action1() {
            @Override
            public void call(Object o) {
                if (o instanceof EditUserinfoEvent) {
                    ZYAgent.onEvent(BaseApplication.getInstance(), "扫描编辑成功");
                    Toast.makeText(mContext, "资料已更新", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
        initView();
        createQRCodeImage();
    }

    private void initView(){
        tipsText = (TextView) findViewById(R.id.tips_new);
        qrcodeImage = (ImageView) findViewById(R.id.qr_code_image);
    }

    public void createQRCodeImage() {
        String url = ApiClient.getEditUserinfoUrl(Preferences.getToken());
        Logger.i("qr code url", url);
        int height = (int) getResources().getDimension(R.dimen.my_px_560);
        Bitmap bp = MethodUtils.createQRImage(url, height, height);
        qrcodeImage.setImageBitmap(bp);
    }



    @Override
    protected void onDestroy() {
        if(subscription!=null){
            subscription.unsubscribe();
        }
        super.onDestroy();
    }
}
