package com.zhongyou.meettvapplicaion.business;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.view.FocusFixedLinearLayoutManager;
import com.zhongyou.meettvapplicaion.view.RecyclerViewTV;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;

public class DocumentActivity extends BasisActivity implements RecyclerViewTV.OnItemListener {

    private Button uploadButton;
    private RecyclerViewTV recyclerViewTV;

    @Override
    public String getStatisticsTag() {
        return "资料列表页";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document);

        initView();
    }

    private void initView(){
        uploadButton = findViewById(R.id.upload);
        uploadButton.setOnClickListener(view -> {
            startActivity(new Intent(mContext, DocUploadActivity.class));
        });

        recyclerViewTV = findViewById(R.id.meeting_doc_list);
        FocusFixedLinearLayoutManager gridlayoutManager = new FocusFixedLinearLayoutManager(this); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recyclerViewTV.setLayoutManager(gridlayoutManager);
        recyclerViewTV.setFocusable(false);
        recyclerViewTV.addItemDecoration(new SpaceItemDecoration((int) (getResources().getDimension(R.dimen.my_px_20)), 0, (int) (getResources().getDimension(R.dimen.my_px_20)), 0));
        recyclerViewTV.setOnItemListener(this);
        recyclerViewTV.setSelectedItemAtCentered(true);

        ApiClient.getInstance().meetingMaterials(TAG, docHttpCallback, "");
    }

    private OkHttpCallback docHttpCallback = new OkHttpCallback<Bucket>() {
        @Override
        public void onSuccess(Bucket meetingBucket) {
            Log.v("doc", "" + meetingBucket.toString());

        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {

    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {

    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

    }
}
