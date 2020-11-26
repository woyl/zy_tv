package com.zhongyou.meettvapplicaion.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.adapter.LabelAdapter;
import com.zhongyou.meettvapplicaion.business.adapter.QRCodeActivity;
import com.zhongyou.meettvapplicaion.business.adapter.StatusesAdapter;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.entities.QRCodeUrl;
import com.zhongyou.meettvapplicaion.entities.Status;
import com.zhongyou.meettvapplicaion.entities.StatusType;
import com.zhongyou.meettvapplicaion.entities.Statuses;
import com.zhongyou.meettvapplicaion.event.ListRequestFocusEvent;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.ActivityUtil;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.QRCodeUtil;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.RecyclerViewTV;
import com.zhongyou.meettvapplicaion.view.SpaceItemDecoration;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.ArrayList;

import rx.Subscription;

public class StatusesActivity extends BasisActivity implements RecyclerViewTV.OnItemListener {

    private TextView uploadButton;
    private Button  hotestButton, likestButton;
    private TextView newstButton;
    private TextView nameText;
    private RecyclerViewTV labelList, contentList;
    private LabelAdapter labelAdapter;
    private StatusesAdapter statusAdapter;
    private GeneralAdapter generalAdapter;
    private Subscription subscription;
    private Boolean dataChange = true;
    private int typeListLastPos = 0;
    private int typeListSelectedPos = 0;
    private View oldView;
    private ArrayList<StatusType> statusTypes;
    private StatusType currentStatusType;
    private ArrayList<Status> statuses = new ArrayList<Status>();

    private int pageNo = 1;
    private int totalNo;

    private int currentTag = 1;
    private int mLoseFoucsPosition=0;

    @SuppressLint("HandlerLeak")
    private Handler mFocusHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1001) {
                onItemSelected(labelList, labelList.getChildAt(0), 0);
            } else if (msg.what == 10) {
                Logger.e("消息类型为10,当前的视屏类型选中的是:"+typeListSelectedPos);
                if (labelAdapter != null) {
                    labelAdapter.setFinish(true);
                    labelList.getChildAt(typeListSelectedPos).setFocusable(true);
                }
            } else if (msg.what == 11) {
                newstButton.setOnKeyListener((view, keyCode, event) -> {
                    Logger.e("handleMessage:newstButton "+keyCode );
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                       return true;
                    }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
                        Logger.e("1  当前typeListSelectedPos是："+typeListSelectedPos);
                        labelList.getChildAt(typeListSelectedPos).requestFocus();
                    }
                    return false;
                });
            }
        }
    };

    @Override
    public String getStatisticsTag() {
        return "金牌商学院列表页";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statuses);

        initView();


        ApiClient.getInstance().statusTypes(TAG, statusTypeCallback);

        subscription = RxBus.handleMessage(o -> {
            if (o instanceof ListRequestFocusEvent) {
                if (((ListRequestFocusEvent) o).status == 0) {
                    labelList.getChildAt(typeListSelectedPos).requestFocus();
                } else if (((ListRequestFocusEvent) o).status == 1) {
                    if (statuses.size() == 0) {
                        uploadButton.requestFocus();
                        setLabelChecked(typeListSelectedPos,true);
                    } else {
                       /* if (statusListLastPos == -1) {
                            if (contentList != null) {
                                if (contentList.getChildAt(0) != null) {
                                    contentList.getChildAt(0).requestFocus();
                                }
                            }
                        } else {
                            oldView.requestFocus();
                        }*/
                    }
                } else if (((ListRequestFocusEvent) o).status == 2) {
                    if (currentTag == 0) {
                        hotestButton.requestFocus();
                    } else if (currentTag == 1) {
                        newstButton.requestFocus();
                    } else if (currentTag == 2) {
                        likestButton.requestFocus();
                    }
                }
            }
        });

    }

    private void initView() {
        nameText = findViewById(R.id.name);
        nameText.setText(Preferences.getUserName());

        uploadButton = findViewById(R.id.upload);
        uploadButton.setOnClickListener(v -> {
            ApiClient.getInstance().statusUploadUrl(TAG, urlCallback);
        });
        uploadButton.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (statuses.size() == 0) {
                        Logger.e("2  当前typeListSelectedPos是："+typeListSelectedPos);
                        labelList.getChildAt(typeListSelectedPos).requestFocus();
                    }
                }
            }
        });

        newstButton = findViewById(R.id.newest);
        newstButton.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Logger.e("newstButton hasFocused");
                statuses.clear();
                currentTag = 1;
                pageNo = 1;
                findVideoByType();
                newstButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
                hotestButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                likestButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                newstButton.setTextColor(Color.WHITE);
                newstButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                hotestButton.setTextColor(Color.parseColor("#5f7c94"));
                likestButton.setTextColor(Color.parseColor("#5f7c94"));
                hotestButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                likestButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


            }
        });


        hotestButton = findViewById(R.id.hotest);
        hotestButton.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {

                statuses.clear();
                currentTag = 0;
                pageNo = 1;
                findVideoByType();
                hotestButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
                newstButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                likestButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                hotestButton.setTextColor(Color.WHITE);
                hotestButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                newstButton.setTextColor(Color.parseColor("#5f7c94"));
                likestButton.setTextColor(Color.parseColor("#5f7c94"));
                newstButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                likestButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
        });

        likestButton = findViewById(R.id.likest);
        likestButton.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                Log.v("button_focus", "likest");
                statuses.clear();
                currentTag = 2;
                pageNo = 1;
                findVideoByType();
                likestButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, R.drawable.ic_selected_tab);
                hotestButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                newstButton.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
                likestButton.setTextColor(Color.WHITE);
                likestButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                newstButton.setTextColor(Color.parseColor("#5f7c94"));
                hotestButton.setTextColor(Color.parseColor("#5f7c94"));
                newstButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                hotestButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                Logger.e("3   当前typeListSelectedPos是："+typeListSelectedPos);
                setLabelChecked(typeListSelectedPos,true);
            }
        });

        labelList = findViewById(R.id.label_list);
        contentList = findViewById(R.id.content_list);

        contentList.setPagingableListener(() -> {
            if (pageNo >= totalNo) {
                contentList.setOnLoadMoreComplete();
                Toast.makeText(StatusesActivity.this, "没有了", Toast.LENGTH_SHORT).show();
                return;
            }
            pageNo++;
            Log.v("pager_listener", "开始加载下一页：pageNo：" + pageNo + ",totalNo:" + totalNo);
            contentList.setOnLoadMoreComplete();
            findVideoByType();
        });

        // 解决快速长按焦点丢失问题.
        LinearLayoutManager gridlayoutManager = new LinearLayoutManager(this, 1, false);
        gridlayoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        labelList.setLayoutManager(gridlayoutManager);
        labelList.setFocusable(false);
        labelList.addItemDecoration(new SpaceItemDecoration(0, (int) (getResources().getDimension(R.dimen.my_px_9)), 0, (int) (getResources().getDimension(R.dimen.my_px_9))));
        labelList.setOnItemListener(this);
        // 设置item在中间移动.
        labelList.setSelectedItemAtCentered(false);
        labelList.setSelectedItemOffset((int) (getResources().getDimension(R.dimen.my_px_101)), (int) (getResources().getDimension(R.dimen.my_px_101)));

        // 解决快速长按焦点丢失问题.
        GridLayoutManager gridlayoutManager2 = new GridLayoutManager(this, 4);
        gridlayoutManager2.setOrientation(GridLayoutManager.VERTICAL);
        contentList.setLayoutManager(gridlayoutManager2);
        contentList.setFocusable(false);
        contentList.addItemDecoration(new SpaceItemDecoration(0, 0, 24, 24));
        contentList.setOnItemListener(this);
        // 设置item在中间移动.
        contentList.setSelectedItemAtCentered(false);
        contentList.setSelectedItemOffset((int) (getResources().getDimension(R.dimen.my_px_101)), (int) (getResources().getDimension(R.dimen.my_px_101)));

        contentList.setOnItemClickListener((parent, itemView, position) -> {
            for (int i = 0; i < parent.getChildCount(); i++) {
                if (i == position) {
                    onItemSelected(parent, itemView, i);
                } else {
                    onItemPreSelected(parent, parent.getChildAt(i), i);
                }
            }
        });


        statusAdapter = new StatusesAdapter(StatusesActivity.this);
        contentList.setAdapter(statusAdapter);
    }

    private OkHttpCallback statusTypeCallback = new OkHttpCallback<Bucket<ArrayList<StatusType>>>() {

        @Override
        public void onSuccess(Bucket<ArrayList<StatusType>> arrayListBucket) {
            statusTypes = arrayListBucket.getData();
            labelAdapter = new LabelAdapter(StatusesActivity.this, statusTypes);
            labelList.setAdapter(new GeneralAdapter(labelAdapter));

            labelAdapter.setOnFocusChangeListener(new LabelAdapter.onFocusChangeListener() {
                @Override
                public void onFocusChange(int position, LabelAdapter.LabelHodler viewHolder,boolean hasFocus) {
                        if (hasFocus){
                            Logger.e("7   当前typeListSelectedPos是："+position);
                            viewHolder.labelButton.setTextColor(getResources().getColor(R.color.blue));
                            viewHolder.mSliderView.setVisibility(View.VISIBLE);
                            onItemSelected(labelList, viewHolder.view, position);
                        }else {
                            viewHolder.labelButton.setTextColor(getResources().getColor(R.color.white));
                            viewHolder.mSliderView.setVisibility(View.INVISIBLE);
                            mLoseFoucsPosition=position;
                        }
                }
            });

            if(statusTypes != null && statusTypes.size()>0){
                mFocusHandler.sendEmptyMessageDelayed(1001, 10);
            }

        }
    };

    private OkHttpCallback urlCallback = new OkHttpCallback<Bucket<QRCodeUrl>>() {

        @Override
        public void onSuccess(Bucket<QRCodeUrl> stringBucket) {
            Log.v("url", "" + stringBucket.getData());
            new Thread(() -> {
                final String filePath = getFileRoot(StatusesActivity.this) + File.separator + "qr_" + System.currentTimeMillis() + ".jpg";
                boolean success = QRCodeUtil.createQRImage(stringBucket.getData().getUrl(), 350, 350, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), filePath);
                if (success) {
                    startActivityForResult(new Intent(StatusesActivity.this, QRCodeActivity.class).putExtra("file_path", filePath), 0x100);
                }
            }).start();
        }
    };

    //文件存储根目录
    private String getFileRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File external = context.getExternalFilesDir(null);
            if (external != null) {
                return external.getAbsolutePath();
            }
        }
        return context.getFilesDir().getAbsolutePath();
    }

    private OkHttpCallback statusCallback(final int position) {
        return new OkHttpCallback<Bucket<Statuses>>() {
            @Override
            public void onSuccess(Bucket<Statuses> statusesBucket) {
                Logger.e("获取视屏数据");
                Logger.i(statusesBucket.toString());
                if (ActivityUtil.isLive(StatusesActivity.this)) {
                    totalNo = statusesBucket.getData().getTotalPage();
                    pageNo = statusesBucket.getData().getPageNo();
                    int totalCount = statusesBucket.getData().getTotalCount();
                    Log.v("pager_info", "total_no:" + totalNo + ", page_no:" + pageNo + ", total_count:" + totalCount);
                    if (pageNo == 1) {
                        statuses.clear();
                        statusAdapter.cleanData();
                    }
                    statuses.addAll(statusesBucket.getData().getPageData());

                    labelAdapter.setFinish(true);

                    Log.e(TAG, "onSuccess: "+statuses.size());
                    if (statuses.size() == 0) {
                        newstButton.setFocusable(true);
                        hotestButton.setFocusable(true);
                        likestButton.setFocusable(true);

                        uploadButton.setFocusable(true);

                        statusAdapter.addData(statuses);
                        labelAdapter.setFinish(true);

                    } else {
                        statusAdapter.addData(statusesBucket.getData().getPageData());
                        contentList.setOnItemClickListener((parent, itemView, position1) -> {
                            Status status = statuses.get(position1);
                            startActivityForResult(new Intent(StatusesActivity.this, StatusActivity.class)
                                    .putParcelableArrayListExtra("statuses", statuses)
                                    .putExtra("status", status)
                                    .putExtra("statusTypeId", currentStatusType.getId())
                                    .putExtra("currentTag", currentTag)
                                    .putExtra("totalNo", totalNo)
                                    .putExtra("pageNo", pageNo), 0x100);
                        });

                    }

                    mFocusHandler.sendEmptyMessageDelayed(10, 200);

                    Logger.e("获取视屏数据之后的position:"+typeListSelectedPos);
                }
            }
        };

    }

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        if (parent == labelList) {
            typeListLastPos = position;
//            labelList.getChildAt(typeListLastPos).setBackground(getResources().getDrawable(R.drawable.bg_button_default));
        } else if (parent == contentList) {
            if (itemView != null) {
                ((TextView) itemView.findViewById(R.id.title)).setMarqueeRepeatLimit(0);
                ((TextView) itemView.findViewById(R.id.title)).setSelected(false);
                ((TextView) itemView.findViewById(R.id.title)).setEllipsize(TextUtils.TruncateAt.END);
            }
        }
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
        if (parent == labelList) {
           Logger.e("视屏类型选中的是"+statusTypes.get(position).getName());
            currentStatusType = statusTypes.get(position);

            typeListSelectedPos = position;
            labelList.getChildAt(typeListSelectedPos).setFocusable(true);
            labelList.getChildAt(position).requestFocus();
            Logger.e("4   当前typeListSelectedPos是："+typeListSelectedPos);

            if (dataChange) {
                labelAdapter.setFinish(false);
                findVideoByType();
            }
            dataChange = true;

        } else if (parent == contentList) {
            oldView = itemView;
            dataChange = false;
            setLabelChecked(typeListSelectedPos,true);
            Logger.e("5  当前typeListSelectedPos是："+typeListSelectedPos);
            if (itemView != null) {
                ((TextView) itemView.findViewById(R.id.title)).setMarqueeRepeatLimit(-1);
                ((TextView) itemView.findViewById(R.id.title)).setSelected(true);
                ((TextView) itemView.findViewById(R.id.title)).setEllipsize(TextUtils.TruncateAt.MARQUEE);
            }
           /* hotestButton.setFocusable(true);
            likestButton.setFocusable(true);
            uploadButton.setFocusable(true);*/
        }
    }


    public void setLabelChecked(int position,boolean focuse){
        Log.e(TAG, "setLabelChecked: "+position );
        LinearLayout childAt = (LinearLayout) labelList.getChildAt(position);
        TextView childAt1= (TextView) childAt.getChildAt(0);
        TextView childAt2= (TextView) childAt.getChildAt(1);
        if (focuse){
            childAt1.setTextColor(getResources().getColor(R.color.blue));
            childAt2.setVisibility(View.VISIBLE);
        }else {
            childAt1.setTextColor(getResources().getColor(R.color.white));
            childAt2.setVisibility(View.INVISIBLE);
        }
    }
    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {

    }

    private long mLastKeyDownTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_UP || (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)) {
            long current = System.currentTimeMillis();
            boolean res;
            if (current - mLastKeyDownTime < 400) {
                res = true;
            } else {
                res = super.onKeyDown(keyCode, event);
                mLastKeyDownTime = current;
            }
            return res;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     *
    * 获取视屏
    * */
    private void findVideoByType() {
        ArrayMap<String, String> params = new ArrayMap<String, String>();
        params.put("typeId", currentStatusType.getId());
        params.put("type", String.valueOf(currentTag + 1));
        params.put("pageSize", "20");
        params.put("pageNo", String.valueOf(pageNo));
        Logger.e("6  当前typeListSelectedPos是："+typeListSelectedPos);
        ApiClient.getInstance().status(TAG, statusCallback(typeListSelectedPos), params);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0x100) {
                findVideoByType();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void setNewsFocus() {
        Log.e(TAG, "setNewsFocus: " );
        newstButton.setFocusable(true);
        newstButton.requestFocus();
        newstButton.setFocusableInTouchMode(true);
        newstButton.requestFocusFromTouch();

    }
}
