package com.zhongyou.meettvapplicaion.utils;

/**
 * Created by whatisjava on 17-1-3.
 */

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.orhanobut.logger.Logger;
import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.BaseException;
import com.zhongyou.meettvapplicaion.BuildConfig;
import com.zhongyou.meettvapplicaion.entities.base.BaseErrorBean;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.Login.HttpLogger;
import com.zhongyou.meettvapplicaion.utils.Login.LoginHelper;
import com.tendcloud.tenddata.TCAgent;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class OkHttpUtil {

    private static OkHttpUtil okHttpUtil;
    private static OkHttpClient okHttpClient;
    private Handler mHandler;
    private static Gson gson;
    private Context mContext;

    private OkHttpUtil() {
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder.connectTimeout(10, TimeUnit.SECONDS);
        clientBuilder.readTimeout(10, TimeUnit.SECONDS);
        clientBuilder.writeTimeout(30, TimeUnit.SECONDS);

        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLogger());
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(httpLoggingInterceptor);

        okHttpClient = clientBuilder.build();

        mHandler = new Handler(Looper.getMainLooper());
        mContext = BaseApplication.getInstance();
    }

    public static OkHttpUtil getInstance() {
        if (okHttpUtil == null) {
            synchronized (OkHttpUtil.class) {
                if (okHttpUtil == null) {
                    okHttpUtil = new OkHttpUtil();
                    gson = new Gson();
                }
            }
        }
        return okHttpUtil;
    }

    /**
     *
     * @param tag
     * @param url
     * @param callback
     * @param params 可变长参数，位置0传body, 位置１传header, 不传为get
     */
    public void get(Object tag, String url, OkHttpCallback callback, Map<String, Object>... params) {
        Request request = buildRequest(tag, url, HttpMethodType.GET, params);
        requestCall(request, callback);
    }

    /**
     *
     * @param tag
     * @param url
     * @param callback
     * @param params 可变长参数，位置0传body, 位置１传header
     */
    public void post(Object tag, String url, OkHttpCallback callback, Map<String, Object>... params) {
        Request request = buildRequest(tag, url, HttpMethodType.POST_JSON, params);
        requestCall(request, callback);
    }


    /**
     *
     * @param tag
     * @param url
     * @param callback
     * @param params 可变长参数，位置0传body, 位置１传header
     */
    public void put(Object tag, String url, OkHttpCallback callback, Map<String, Object>... params) {
        Request request = buildRequest(tag, url, HttpMethodType.PUT_JSON, params);
        requestCall(request, callback);
    }

    /**
     *
     * @param tag
     * @param url
     * @param callback
     * @param params 可变长参数，位置0传body, 位置１传header
     */
    public void delete(Object tag, String url, OkHttpCallback callback, Map<String, Object>... params) {
        Request request = buildRequest(tag, url, HttpMethodType.DELETE_JSON, params);
        requestCall(request, callback);
    }

    private void requestCall(final Request request, final OkHttpCallback callback) {

        callback.onStart();

        Call call = okHttpClient.newCall(request);
        if (call.isExecuted() || call.isCanceled()) {
            return;
        }
        call.enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                if (e != null) {
                    if (e.getMessage() != null && e.getMessage().contains("Failed to connect to")) {
                        callbackFailure(-1, callback, new BaseException("连接服务器异常，请检查网络设置", e));
                    } else if(e.getMessage() != null && e.getMessage().contains("Unable to resolve host")) {
                        callbackFailure(-1, callback, new BaseException("无法解析主机，请检查网络设置", e));
                    } else {
                        callbackFailure(-1, callback, new BaseException(e.getMessage(), e));
                    }
                } else {
                    callbackFailure(-1, callback, new BaseException("服务器繁忙，请稍后再试", e));
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.code() == 502) {
                    BaseException exception = new BaseException("502网关错误，请检查网络设置");
                    callbackFailure(response.code(), callback, exception);
                    return;
                }
                if (response.body() != null) {
                    String resString = response.body().string();
                    try {
                        BaseErrorBean bucket = gson.fromJson(resString, BaseErrorBean.class);
                        if (bucket != null) {
                            if (bucket.getErrcode() == 0) {
                                Object object = gson.fromJson(resString, callback.mType);
                                callbackSuccess(object, callback);
                            } else if(bucket.getErrcode() == 40001 | bucket.getErrcode() == 40003) {
                                Logger.e("----------"+request.url()+"-----------");
                              LoginHelper.logout();
                            } else {
                                callbackFailure(response.code(), callback, new BaseException(bucket.getErrmsg(), bucket.getErrcode()));
                            }
                        } else {
                            callbackFailure(response.code(), callback, new BaseException("服务器繁忙，请稍后再试"));
                        }
                    } catch (JsonSyntaxException e) {
                        callbackFailure(response.code(), callback, new BaseException(e.getMessage(), e));
                    }
                } else {
                    callbackFailure(response.code(), callback, new BaseException("response body is null"));
                }
            }
        });
    }

    private void runOnUiThread(Runnable task) {
        mHandler.post(task);
    }

    private void callbackSuccess(final Object o, final OkHttpCallback callback) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(o);
                callback.onFinish();
            }
        });
    }

    private void callbackFailure(final int code, final OkHttpCallback callback, final BaseException e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(code, e);
                callback.onFinish();
            }
        });
    }



    /**
     *
     * @param tag
     * @param url
     * @param type
     * @param params 数组，位置0传body, 位置１传header, get请求位置0 为header
     * @return
     */




    private Request buildRequest(Object tag, String url, HttpMethodType type, Map<String, Object>... params) {
        Request.Builder builder = new Request.Builder().url(url);
        addHeader(mockHeaders(), builder);
        if (tag != null) {
            builder.tag(tag);
        }
        if (type == HttpMethodType.GET) {
            builder.get();
        } else if (type == HttpMethodType.POST_JSON) {
            if (params.length > 0){
                builder.post(buildRequestBody(gson.toJson(params[0])));
            } else {
                builder.post(buildRequestBody("{}"));
            }
        } else if (type == HttpMethodType.PUT_JSON) {
            if (params.length > 0){
                builder.put(buildRequestBody(gson.toJson(params[0])));
            } else {
                builder.put(buildRequestBody("{}"));
            }
        } else if (type == HttpMethodType.DELETE_JSON) {
            if (params.length > 0){
                builder.delete(buildRequestBody(gson.toJson(params[0])));
            } else {
                builder.delete();
            }
        }
        return builder.build();
    }

    /**
     * 为请求添加header头信息
     *
     * @param headers
     * @param builder
     * @return
     */
    private Request.Builder addHeader(Map<String, Object> headers, Request.Builder builder) {
        if (headers != null && headers.size() > 0) {
            for (Map.Entry<String, Object> entry : headers.entrySet()) {
                builder.addHeader(entry.getKey(), String.valueOf(entry.getValue()));
            }
            return builder;
        }
        return builder;
    }

    /**
     * 通过Map的键值对构建请求对象的body
     *
     * @param params Map
     * @return RequestBody
     */
    private RequestBody buildRequestBody(Map<String, Object> params) {
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, Object> entity : params.entrySet()) {
                builder.add(entity.getKey(), String.valueOf(entity.getValue()));
            }
        }
        return builder.build();
    }

    /**
     * 通过json字符串创建请求对象的body
     *
     * @param jsonString String
     * @return RequestBody
     */
    private RequestBody buildRequestBody(String jsonString) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonString);
    }

    private MultipartBody buildMultipartRequestBody(Map<String, String> params) {
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            for (Map.Entry<String, String> entity : params.entrySet()) {
                multipartBuilder.addFormDataPart(entity.getKey(), entity.getValue());
            }
        }
        return multipartBuilder.build();
    }

    /**
     * 这个枚举用于指明是哪一种提交方式
     */
    private enum HttpMethodType {
        GET,
        POST,
        POST_MAP,
        POST_JSON,
        POST_FILE,
        PUT,
        PUT_JSON,
        DELETE,
        DELETE_JSON
    }

    /**
     * 根据Tag取消请求
     *
     * @param tag
     */
    public void cancelTag(Object tag) {
        Log.d("cancel tag--->", tag.toString());
        for (Call call : okHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                Log.d("cancel ok--->", "the queued tag " + tag.toString() + " is canceled success");
            }
        }
        for (Call call : okHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                Log.d("cancel ok--->", "the running tag " + tag.toString() + " is canceled success");
            }
        }
    }

    private Map<String, Object> mockHeaders(){
        Map<String, Object> headers = new HashMap<String, Object>();
        headers.put("Content-Type", "application/json; charset=UTF-8");
        String deviceID = UUIDUtils.getUUID(BaseApplication.getInstance());
        headers.put("DeviceUuid", "" + deviceID);
        headers.put("User-Agent", "HRZY_HOME"
                + "_"
                + mContext.getPackageName()
                + "_"
                + BuildConfig.VERSION_NAME
                + "_"
                + TCAgent.getDeviceId(mContext)
                + "(android_OS_" + Build.VERSION.RELEASE + ";" + Build.MANUFACTURER + "_" + Build.MODEL + ")");
        if (!TextUtils.isEmpty(Preferences.getToken())) {
            headers.put("Authorization", "Token " + Preferences.getToken());
        }
        return headers;
    }

}
