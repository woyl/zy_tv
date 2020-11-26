package com.zhongyou.meettvapplicaion.utils.helper;

import android.content.Context;
import android.support.annotation.DimenRes;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.zhongyou.meettvapplicaion.BaseApplication;
import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.persistence.Preferences;
import com.zhongyou.meettvapplicaion.utils.DensityUtil;
import com.zhongyou.meettvapplicaion.utils.HalfType;
import com.zhongyou.meettvapplicaion.utils.Logger;
import com.zhongyou.meettvapplicaion.utils.RoundCornerTransform;
import com.squareup.picasso.Picasso;

/**
 * Created by wufan on 2017/7/26.
 */

public class ImageHelper {
    public static final String TAG = "ImageHelper";

    public static boolean isContextEmpty() {
        if (BaseApplication.getInstance() == null) {
            Log.e(TAG, "com.hezy.guidesale.BaseApplication.getInstance()==null");
            return true;
        } else {
            return false;
        }

    }

    /**
     * 先缩放再裁剪等同于centerCrop效果
     *
     * @param url
     * @return
     */
    public static String getThumb(String url) {
        return url + "?imageMogr2/auto-orient/thumbnail/1920x1080";
    }

    public static Context getContext() {
        return BaseApplication.getInstance();
    }

    /**
     * 判断url和host为空
     *
     * @param url
     * @return
     */
    public static boolean isEmpty(String url) {
        return TextUtils.isEmpty(url) || TextUtils.isEmpty(Preferences.getImgUrl());
    }


    public static String getUrlJoin(String url) {
        if (url != null) {
            if (url.contains("http")) {
                return url;
            } else {
                return Preferences.getImgUrl() + url;
            }
        } else {
            return null;
        }
    }

    //    public static void loadImageRound(String url, ImageView imageView) {
//        if (!ImageHelper.isContextEmpty()) {
//            Picasso.with(getContext()).load(url).placeholder(R.drawable.image_loading_round).error(R.drawable.image_loading_round).transform(new RoundCornerTransform((int)getContext().getResources().getDimension(R.dimen.my_px_18), 0, HalfType.ALL)).into(imageView);
//        }
//    }
//
//
//
//    public static void loadImageRound(String url, int w, int h , ImageView imageView){
//        if(ImageHelper.isEmpty(url)){
//            loadImageRoundResError(imageView);
//        }else{
//            url = ImageHelper.getUrlJoinAndThumAndCrop(url,w,h);
//            loadImageRound(url,imageView);
//        }
//    }
//

    /**
     *
     * @param url
     * @param w_id
     * @param h_id
     * @param imageView
     */
    public static void loadImageDpId(String url, @DimenRes int w_id, @DimenRes int h_id, ImageView imageView) {
        if (!ImageHelper.isContextEmpty() && !ImageHelper.isEmpty(url)) {
            int w_dp = (int) getContext().getResources().getDimension(w_id);
            int h_dp = (int) getContext().getResources().getDimension(h_id);
//            int w_px = DensityUtil.dip2px(getContext(), w_dp);
//            int h_px = DensityUtil.dip2px(getContext(), h_dp);
            url = ImageHelper.getUrlJoinAndThumAndCrop(url, w_dp, h_dp);
            Picasso.with(getContext()).load(url).into(imageView);
        }
    }

    /**
     *圆角矩形
     * @param url
     * @param w_id
     * @param h_id
     * @param imageView
     */
    public static void loadImageDpIdRound(String url, @DimenRes int w_id, @DimenRes int h_id, ImageView imageView) {
        if (!ImageHelper.isContextEmpty() && !ImageHelper.isEmpty(url)) {
            int w_dp = (int) getContext().getResources().getDimension(w_id);
            int h_dp = (int) getContext().getResources().getDimension(h_id);
//            int w_px = DensityUtil.dip2px(getContext(), w_dp);
//            int h_px = DensityUtil.dip2px(getContext(), h_dp);
            url = ImageHelper.getUrlJoinAndThumAndCrop(url, w_dp, h_dp);
            int round_dp = (int) getContext().getResources().getDimension(R.dimen.my_px_20);
            int round_px = DensityUtil.dip2px(getContext(), round_dp);
            Logger.i(TAG,"loadImageDpIdRound "+url);
            Picasso.with(getContext()).load(url).transform(new RoundCornerTransform(round_px, 0, HalfType.ALL)).into(imageView);
        }
    }

    /**
     *圆角矩形并且blur
     * @param url
     * @param w_id
     * @param h_id
     * @param imageView
     */
    public static void loadImageDpIdBlur(String url, @DimenRes int w_id, @DimenRes int h_id, ImageView imageView) {
        if (!ImageHelper.isContextEmpty() && !ImageHelper.isEmpty(url)) {
            int w_dp = (int) getContext().getResources().getDimension(w_id);
            int h_dp = (int) getContext().getResources().getDimension(h_id);
            int w_px = DensityUtil.dip2px(getContext(), w_dp);
            int h_px = DensityUtil.dip2px(getContext(), h_dp);
            url = ImageHelper.getUrlJoinAndThumAndCropAndBlur(url, w_dp, h_dp);
            Logger.i(TAG,"loadImageDpIdBlur  "+w_dp+" "+h_dp+" "+w_px+" "+h_px);
            Logger.i(TAG,"loadImageDpIdBlur "+url);
            Picasso.with(getContext()).load(url).into(imageView);
        }
    }


    /**
     * 获得拼接host和缩放thum的url
     *
     * @param url
     * @param w
     * @param h
     * @return
     */
    public static String getUrlJoinAndThum(String url, int w, int h) {
        url = getUrlJoin(url);
        if (w < 0 || w > 1920 || h < 0 || h > 1080) {
            return url;
        } else {
            url = url + "?imageMogr2/auto-orient/thumbnail/" + w + "x" + h + "/interlace/" + 1;
            return url;
        }
    }


    public static String getUrlJoinAndThumAndCrop(String url, int w, int h) {
        url = getUrlJoin(url);
        return getThumAndCrop(url, w, h);
    }

    /**
     * 先缩放再裁剪等同于centerCrop效果
     *
     * @param url
     * @param w
     * @param h
     * @return
     */
    public static String getThumAndCrop(String url, int w, int h) {
        if (w < 0 || w > 1920 || h < 0 || h > 1080) {
            return url;
        } else {
            url = url + "?imageMogr2/auto-orient/thumbnail/!" + w + "x" + h + "r" + "/gravity/Center/crop/" + w + "x" + h + "/interlace/" + 1;
            return url;
        }
    }

    public static String getThumAndCropAndBlur(String url, int w, int h) {
        if (w < 0 || w > 1920 || h < 0 || h > 1080) {
            return url;
        } else {
            url = url + "?imageMogr2/auto-orient/thumbnail/!" + w + "x" + h + "r" + "/gravity/Center/crop/" + w + "x" + h + "/blur/" + 50 + "x" + 20 + "/interlace/" + 1;
            return url;
        }
    }

    public static String getUrlJoinAndThumAndCropAndBlur(String url, int w, int h) {
        url = getUrlJoin(url);
        return getThumAndCropAndBlur(url, w, h);
    }

    public static String videoFrameUrl(String url, int w, int h){
        return url + "?vframe/jpg/offset/10/w/" + w + "/h/" + h;

    }
    public static String videoImageFromUrl(String url, int w, int h){
        return url + "?vframe/jpg/offset/5/w/" + w + "/h/" + h;

    }

}
