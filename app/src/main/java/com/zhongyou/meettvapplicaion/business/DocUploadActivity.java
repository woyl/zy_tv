package com.zhongyou.meettvapplicaion.business;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.Materials;
import com.zhongyou.meettvapplicaion.entities.MeetingMaterialsPublish;
import com.zhongyou.meettvapplicaion.entities.StorageBean;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class DocUploadActivity extends BasisActivity {

    @Override
    public String getStatisticsTag() {
        return "文件夹列表页";
    }

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);

        init();

        ArrayList<StorageBean> storageBeans = StorageUtils.getStorageData(this);
        if (storageBeans.size() > 0) {
            File[] files = new File(storageBeans.get(0).getPath()).listFiles();
            for (File file : files) {
                if (!file.isHidden() && file.isDirectory()) {
                    Log.v("filename--->", file.getName());

                } else {
//                    Log.v("file", file.getName());
                }
            }
        } else {
            Log.v("storage_size", "storage size is zero");
        }
    }

    private void init() {
        listView = findViewById(R.id.file_list_view);

        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("http://image.i.teq6.com/dz/msjt/7c4502fd-963c-4182-ab81-8fc16da9e7c7.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/e25b2028-63b7-469e-8076-746f9ac066bc.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/cb4736ea-d702-4c5c-b052-b2c0762d582d.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/fc32a581-32e8-4115-95a2-ef4876dc6c49.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/c6f4edce-22fe-45de-9c21-c2e27f63e7fe.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/83592ee5-f7bb-4015-b98a-cd3725c2e4e0.png");
        arrayList.add("http://image.i.teq6.com/dz/msjt/756204ff-2c85-4b16-8b89-36c92a833e75.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/ca4b8a28-5760-4cdf-a3de-c8547bdb35fe.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/ae894e8b-e940-4b63-b2a2-1a38c18c530b.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/eb649d47-fcbe-4483-b14b-a28eaeff252c.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/d4267ec1-526f-43d1-aa5a-b8da4f3cb42f.jpg");
        arrayList.add("http://image.i.teq6.com/dz/msjt/5769d734-f992-4803-a915-d51197e75eda.jpg");

        findViewById(R.id.upload).setOnClickListener(view -> {
            HashMap<String, Object> params = new HashMap();
            ArrayList<MeetingMaterialsPublish> meetingMaterialsPublishes = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                MeetingMaterialsPublish meetingMaterialsPublish = new MeetingMaterialsPublish();
                meetingMaterialsPublish.setUrl(arrayList.get(i));
                meetingMaterialsPublish.setName("image-" + i + "-" + arrayList.get(i).substring(arrayList.get(i).lastIndexOf("/") + 1));
                meetingMaterialsPublishes.add(meetingMaterialsPublish);
            }
            params.put("meetingMaterialsPublishs", meetingMaterialsPublishes);
            ApiClient.getInstance().meetingMaterialUpload(TAG, meetingDocCallback, params);
        });

    }

    private OkHttpCallback meetingDocCallback = new OkHttpCallback<Bucket<Materials>>() {
        @Override
        public void onSuccess(Bucket<Materials> bucket) {
            Log.v("doc", "" + bucket.toString());

        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(DocUploadActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

}
