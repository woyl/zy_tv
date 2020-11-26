package com.zhongyou.meettvapplicaion.business.adapter;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.BasisActivity;
import com.zhongyou.meettvapplicaion.event.PublishEvent;
import com.zhongyou.meettvapplicaion.utils.RxBus;

import rx.Subscription;

/**
 * Created by wufan on 2017/8/25.
 */

public class QRCodeActivity extends BasisActivity {

    private TextView tipsText;
    private ImageView qrcodeImage;
    private Subscription subscription;
    private String filePath;

    @Override
    public String getStatisticsTag() {
        return "上传资源二维码";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_wechat);

        filePath = getIntent().getStringExtra("file_path");

        subscription = RxBus.handleMessage(o -> {
            if (o instanceof PublishEvent) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(QRCodeActivity.this, "" + ((PublishEvent) o).getMsg(), Toast.LENGTH_SHORT).show();
                    }
                });
                setResult(RESULT_OK);
                finish();
            }
        });

        initView();
        createQRCodeImage();
    }

    private void initView() {
        tipsText = findViewById(R.id.tips_new);
        qrcodeImage = findViewById(R.id.qr_code_image);
    }

    public void createQRCodeImage() {
        tipsText.setText("微信扫码上传视频/图片");
        qrcodeImage.setImageBitmap(BitmapFactory.decodeFile(filePath));
    }


    @Override
    protected void onDestroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        super.onDestroy();
    }

}
