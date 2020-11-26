package com.zhongyou.meettvapplicaion.business;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zhongyou.meettvapplicaion.ApiClient;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.adapter.ForumMeetingAdapter;
import com.zhongyou.meettvapplicaion.entities.Bucket;
import com.zhongyou.meettvapplicaion.event.ForumActivityCloseEvent;
import com.zhongyou.meettvapplicaion.entities.ForumComment;
import com.zhongyou.meettvapplicaion.entities.ForumContent;
import com.zhongyou.meettvapplicaion.entities.ForumRevokeContent;
import com.zhongyou.meettvapplicaion.entities.ForumViewLog;
import com.zhongyou.meettvapplicaion.entities.PageData;
import com.zhongyou.meettvapplicaion.event.ForumGetMsgEvent;
import com.zhongyou.meettvapplicaion.event.ForumRevocationMsgEvent;
import com.zhongyou.meettvapplicaion.utils.OkHttpCallback;
import com.zhongyou.meettvapplicaion.utils.RxBus;
import com.zhongyou.meettvapplicaion.utils.StringUtils;
import com.zhongyou.meettvapplicaion.utils.ToastUtils;
import com.zhongyou.meettvapplicaion.utils.listener.RecyclerViewScrollListener;
import com.zhongyou.meettvapplicaion.utils.statistics.ZYAgent;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import rx.Subscription;

/**
 * @author Dongce
 * create time: 2018/11/20
 */
public class ForumActivity extends AppCompatActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private Subscription subscription;

    private RecyclerView recycleviewForum;
    private Dialog imageReviewDialog;
    private ImageView imageReview;
    private String meetingId;
    public static final String INTENT_KEY_FORUM = "forum";
    private ConstraintLayout constraintLayout_forum_comment_normal, constraintLayout_forum_comment_enter;
    private EditText edt_forum_comment;
    private Button btn_forum_comment_send;
    private ForumMeetingAdapter forumMeetingAdapter;
    private LinkedList<PageData> forums = new LinkedList<>();
    private ForumContent forumContent;
    private boolean isShowEnterLayout;

    public enum LoadingStatus {LoadingNewMsg, LoadingHistoryMsg, RevocationMsg}

    //分页信息
    private final int PAGE_SIZE = 10;
    private final int PAGE_NO = 1;
    private int pageNo = PAGE_NO;


    private void initForumPage() {
        pageNo = PAGE_NO;
        forumMeetingAdapter.clearData();
        recycleviewForum.setAdapter(forumMeetingAdapter);
    }

    private boolean nextPage() {
        if (pageNo >= forumContent.getTotalPage()) {
            ToastUtils.showToast("没有更多数据了！");
            return false;
        }
        pageNo += PAGE_NO;
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);

        meetingId = getIntent().getStringExtra(INTENT_KEY_FORUM);

        initView();
        initData();
        initControl();
        requestRecord(LoadingStatus.LoadingHistoryMsg);
        subscription = RxBus.handleMessage(o -> {
            if (o instanceof ForumGetMsgEvent) {
                PageData pageData = ((ForumGetMsgEvent) o).getPageData();
                if (meetingId.equals(pageData.getMeetingId())){
                    requestRecord(LoadingStatus.LoadingNewMsg);
                }
            } else if (o instanceof ForumRevocationMsgEvent) {
                ForumRevokeContent forumRevokeContent= ((ForumRevocationMsgEvent) o).getForumRevokeContent();
                if (meetingId.equals(forumRevokeContent.getMeetingId())){
                    requestRecord(LoadingStatus.RevocationMsg);
                }
            } else if (o instanceof ForumActivityCloseEvent){
                ForumActivity.this.finish();
            }
        });
    }

    private void requestRecord(LoadingStatus loadingStatus) {
        Map<String, String> params = new HashMap<>();
        switch (loadingStatus) {
            case LoadingHistoryMsg:
                params.put(ApiClient.PAGE_NO, String.valueOf(pageNo));
                params.put(ApiClient.PAGE_SIZE, String.valueOf(PAGE_SIZE));
                params.put("meetingId", meetingId);
                ApiClient.getInstance().getForumContent(this, params, loadingForumHistoryMsgCallback);
                break;
            case LoadingNewMsg:
                params.put(ApiClient.PAGE_NO, "1");
                params.put(ApiClient.PAGE_SIZE, "1");
                params.put("meetingId", meetingId);
                ApiClient.getInstance().getForumContent(this, params, loadingForumNewMsgCallback);
                break;
            case RevocationMsg:
                forumMeetingAdapter.revocationData();
                readForumMsg();
                break;
        }
    }

    private void initView() {
        constraintLayout_forum_comment_normal = findViewById(R.id.constraintLayout_forum_comment_normal);
        constraintLayout_forum_comment_enter = findViewById(R.id.constraintLayout_forum_comment_enter);
        edt_forum_comment = findViewById(R.id.edt_forum_comment);
        btn_forum_comment_send = findViewById(R.id.btn_forum_comment_send);
        recycleviewForum = findViewById(R.id.recycleview_forum);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recycleviewForum.setLayoutManager(mLayoutManager);
        // 设置ItemAnimator
        recycleviewForum.setItemAnimator(new DefaultItemAnimator());
        recycleviewForum.setHasFixedSize(true);
        initImageReviewDialog();
    }

    private void initImageReviewDialog() {
        imageReviewDialog = new Dialog(this);
        Window dialogWindow = imageReviewDialog.getWindow();
        assert dialogWindow != null;
        dialogWindow.requestFeature(Window.FEATURE_NO_TITLE);
        imageReview = new ImageView(this);
        dialogWindow.setContentView(imageReview);
        dialogWindow.setGravity(Gravity.CENTER);
    }

    private void initData() {
        isShowEnterLayout = false;
        forumMeetingAdapter = new ForumMeetingAdapter(getApplicationContext(), forumItemOnClickListener);
        forumMeetingAdapter.addDataAtFirst(forums);
        initForumPage();
    }

    private void initControl() {
        constraintLayout_forum_comment_normal.setOnClickListener(this);
        btn_forum_comment_send.setOnClickListener(this);
        edt_forum_comment.setOnFocusChangeListener(this);
        edt_forum_comment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendForumCommentMessage(edt_forum_comment.getText().toString());
            }
            return false;
        });
        recycleviewForum.addOnScrollListener(forumRecyclerViewScrollListener);
    }

    private RecyclerViewScrollListener forumRecyclerViewScrollListener = new RecyclerViewScrollListener() {
        @Override
        public void onScrollToTop() {
            if (nextPage()) {
                requestRecord(LoadingStatus.LoadingHistoryMsg);
            }
        }
    };

    private ForumMeetingAdapter.OnItemClickListener forumItemOnClickListener = new ForumMeetingAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, PageData pageData) {
            if (pageData == null) {
                String errMsg = "获取讨论区数据失败!";
                ZYAgent.onEvent(ForumActivity.this, errMsg);
                ToastUtils.showToast(errMsg);
                return;
            }
            if (pageData.getType() == PageData.TYPE_IMAGE) {
                Picasso.with(ForumActivity.this).load(pageData.getContent()).config(Bitmap.Config.RGB_565).into(imageReview);
                imageReviewDialog.show();
            }
        }
    };

    /**
     * 加载历史记录回调函数
     */
    private OkHttpCallback<Bucket<ForumContent>> loadingForumHistoryMsgCallback = new OkHttpCallback<Bucket<ForumContent>>() {
        @Override
        public void onSuccess(Bucket<ForumContent> forumContentBaseBean) {
            ZYAgent.onEvent(getApplicationContext(), "获得到讨论区信息历史记录");
            //标记消息已读
            readForumMsg();

            forumContent = forumContentBaseBean.getData();
            LinkedList<PageData> pageDatas = forumContent.getPageData();

            if (pageDatas.size() == 0) {
                //OSG-445，根据需求再次修改，默认入口变回展示状态，并添加默认文字，作为讨论区入口。默认不判断为空情况
                return;
            }
            forumMeetingAdapter.addDataAtFirst(pageDatas);
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            String errMsg = "未获得到讨论区信息历史记录，请重试";
            ZYAgent.onEvent(getApplicationContext(), errMsg);
            ToastUtils.showToast(errMsg);
        }
    };

    /**
     * 加载最新数据回调函数
     */
    private OkHttpCallback<Bucket<ForumContent>> loadingForumNewMsgCallback = new OkHttpCallback<Bucket<ForumContent>>() {
        @Override
        public void onSuccess(Bucket<ForumContent> forumContentBaseBean) {
            ZYAgent.onEvent(getApplicationContext(), "获得到讨论区新数据");
            //标记消息已读
            readForumMsg();

            forumContent = forumContentBaseBean.getData();
            LinkedList<PageData> pageDatas = forumContent.getPageData();

            if (pageDatas.size() == 0) {
                String errMsg = "讨论区获取新数据为空，请重试";
                ZYAgent.onEvent(getApplicationContext(), errMsg);
                ToastUtils.showToast(errMsg);
                return;
            }
            forumMeetingAdapter.addDataAtLast(pageDatas);

            //处理光标滚动位置
            recycleviewForum.smoothScrollToPosition(forumMeetingAdapter.getItemCount() - 1);
//            System.out.println("消息总数：" + (forumMeetingAdapter.getItemCount() - 1));
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            String errMsg = "未获得到讨论区新数据，请重试";
            ZYAgent.onEvent(getApplicationContext(), errMsg);
            ToastUtils.showToast(errMsg);
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        StringUtils.softKeyboardManager(getApplicationContext(), edt_forum_comment, false);
    }

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        if (imageReviewDialog != null && imageReviewDialog.isShowing()) {
            imageReviewDialog.cancel();
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.constraintLayout_forum_comment_normal:
                showEnterLayout();
                break;
            case R.id.btn_forum_comment_send:
                sendForumCommentMessage(edt_forum_comment.getText().toString().trim());
                break;
        }
    }

    private void sendForumCommentMessage(String message) {
        if (TextUtils.isEmpty(message)) {
            ToastUtils.showToast("请输入内容再发送！");
            return;
        }
        if (message.length() > 500){
            ToastUtils.showToast("文字超过500字符，请缩减一些文字吧");
            return;
        }

        ArrayMap<String, Object> params = new ArrayMap<>();
        params.put("meetingId", meetingId);
        params.put("ts", System.currentTimeMillis());
        params.put("type", 0);
        params.put("content", message);
        params.put("atailUserId", "");
        ApiClient.getInstance().sendForumContext(this, params, forumCommentCallback);
    }

    private OkHttpCallback<Bucket<ForumComment>> forumCommentCallback = new OkHttpCallback<Bucket<ForumComment>>() {
        @Override
        public void onSuccess(Bucket<ForumComment> forumCommentBucket) {
            ZYAgent.onEvent(ForumActivity.this, "评论发送成功");
            edt_forum_comment.setText("");
        }

        @Override
        public void onFailure(int errorCode, BaseException exception) {
            super.onFailure(errorCode, exception);
            String errorMsg = "评论发送失败，错误码：" + errorCode + " , Error：" + exception.getMessage();
            ZYAgent.onEvent(ForumActivity.this, errorMsg);
            ToastUtils.showToast(errorMsg);
        }
    };

    private void showEnterLayout() {
        constraintLayout_forum_comment_normal.setVisibility(View.GONE);
        constraintLayout_forum_comment_enter.setVisibility(View.VISIBLE);
        edt_forum_comment.setFocusable(true);
        edt_forum_comment.setFocusableInTouchMode(true);
        edt_forum_comment.requestFocus();

        isShowEnterLayout = true;
    }

    private void showNormalLayout() {
        constraintLayout_forum_comment_normal.setVisibility(View.VISIBLE);
        constraintLayout_forum_comment_enter.setVisibility(View.GONE);
        edt_forum_comment.setFocusable(false);
        edt_forum_comment.setFocusableInTouchMode(false);
        edt_forum_comment.clearFocus();
        isShowEnterLayout = false;

        constraintLayout_forum_comment_normal.requestFocus();
    }

    /**
     * 已读信息标记
     */
    private void readForumMsg() {
        ApiClient.getInstance().sendViewLog(this, meetingId, new OkHttpCallback<Bucket<ForumViewLog>>() {
            @Override
            public void onSuccess(Bucket<ForumViewLog> forumViewLogBucket) {
                String errorMsg = "讨论区已读标记成功";
                ZYAgent.onEvent(ForumActivity.this, errorMsg);
            }

            @Override
            public void onFailure(int errorCode, BaseException exception) {
                super.onFailure(errorCode, exception);
                String errorMsg = "讨论区已读标记失败";
                ZYAgent.onEvent(ForumActivity.this, errorMsg);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (isShowEnterLayout) {
            showNormalLayout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.edt_forum_comment:
                if (hasFocus) {
                    StringUtils.softKeyboardManager(getApplicationContext(), edt_forum_comment, true);
                } else {
                    StringUtils.softKeyboardManager(getApplicationContext(), edt_forum_comment, false);
                }
                break;
        }
    }

}
