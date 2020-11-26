package com.zhongyou.guide.phone.meetingcamera.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;

import com.zhongyou.guide.phone.meetingcamera.activity.Camera1ByServiceActivity;
import com.zhongyou.guide.phone.meetingcamera.camera.CameraHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author Dongce
 * create time: 2018/11/7
 */
public class Camera1Service extends Service {

    private final String TAG = "Camera1Service";

    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private File mOutputFile;
    private final IBinder mBinder = new LocalBinder();

    //会议实况图片压缩率，设置为2的指数。tv端默认压缩率是8
    private int imageCompressionRatio = 8;

    public static ITakePictureCallback getiTakePictureCallback() {
        return iTakePictureCallback;
    }

    public static void setiTakePictureCallback(ITakePictureCallback iTakePictureCallback) {
        Camera1Service.iTakePictureCallback = iTakePictureCallback;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "server 已绑定");
        imageCompressionRatio = intent.getIntExtra(Camera1ByServiceActivity.KEY_IMAGE_COMPRESSION_RATIO, imageCompressionRatio);
        return mBinder;
    }


    public class LocalBinder extends Binder {
        public Camera1Service getService() {
            return Camera1Service.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "server 已被创建");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "server 已解绑");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "server 已销毁");
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }

    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        this.mSurfaceHolder = surfaceHolder;
        initCamera();
    }

    private void initCamera() {
        try {
            if (mCamera == null) {
                mCamera = CameraHelper.getDefaultFrontFacingCameraInstance();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mCamera == null) {
            Log.i(TAG, "没有检测到摄像头，请检查摄像头设备");
            if (getiTakePictureCallback() != null) {
                getiTakePictureCallback().onTakePictureDone("");
            }
            return;
        }

        int maxWidth = 0, maxHeight = 0, temp;
        List<Camera.Size> supportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();
        for (Camera.Size size : supportedPictureSizes) {
            temp = size.width;
            if (maxWidth < temp)
                maxWidth = temp;
        }
        for (Camera.Size size : supportedPictureSizes) {
            if (size.width == maxWidth) {
                maxHeight = size.height;
            }
        }
        Log.i(TAG, "maxWidth=" + maxWidth + ", maxHeight=" + maxHeight);

        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            //设置图片格式
            parameters.setPictureFormat(ImageFormat.JPEG);
            //设置图片的质量
            parameters.set("jpeg-quality", 90);
            //设置照片的大小
            parameters.setPictureSize(maxWidth, maxHeight);
            mCamera.setParameters(parameters);

            //通过SurfaceView显示预览
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //开始预览
            mCamera.startPreview();

            Log.i(TAG, "打开了摄像头预览");

            takePicture();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void takePicture() {
        if (mCamera == null) {
            Log.i(TAG, "摄像头为空，拍摄失败");
            return;
        }
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    try {
                        Log.i(TAG, "对焦完毕");
                        camera.takePicture(null, new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(final byte[] data, Camera camera) {//是否保存原始图片的信息
                                if (data != null) {
                                    Log.i(TAG, "raw无压缩原文件：data=" + data + "，大小：" + data.length);
                                    mCamera.unlock();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            savePicture(compressImage(data));
                                        }
                                    }).start();
                                } else {
                                    Log.i(TAG, "raw无压缩原文件为空，不保存");
                                }
                            }
                        }, pictureJPEGCallback);
                    } catch (Exception e) {
                        Log.i(TAG, "拍照未完成");
                    }
                }
            }
        });
    }

    private byte[] compressImage(byte[] fileBytes) {
        //压缩图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = imageCompressionRatio;
        options.inJustDecodeBounds = false;
        Bitmap afterBitmap = BitmapFactory.decodeByteArray(fileBytes, 0, fileBytes.length, options);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        afterBitmap.compress(Bitmap.CompressFormat.PNG, 70, baos);
        byte[] bytes = baos.toByteArray();

        // 先判断是否已经回收
        if (afterBitmap != null && !afterBitmap.isRecycled()) {
            // 回收并且置为null
            afterBitmap.recycle();
            afterBitmap = null;
        }
        System.gc();

        return bytes;
    }

    private Camera.PictureCallback pictureJPEGCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            savePicture(compressImage(data));
        }
    };

    private void savePicture(byte[] data) {
        mOutputFile = CameraHelper.getOutputMediaFile(CameraHelper.MEDIA_TYPE_IMAGE);
        if (mOutputFile == null) {
            Log.i(TAG, "【文件创建为空，不能存储数据】");
            return;
        }

        Log.i(TAG, "拍照完毕：data=" + data + "，大小：" + data.length);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(mOutputFile.getAbsolutePath());
            fileOutputStream.write(data);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "【文件未找到，图片写入失败】");
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG, "【图片文件写入失败】");
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                Log.i(TAG, "【图片文件关闭失败】");
                e.printStackTrace();
            }

            String msg = "照片文件存储在：" + mOutputFile.getPath() + ", 文件大小：" + mOutputFile.length();
            Log.i(TAG, msg);

            if (getiTakePictureCallback() != null) {
                getiTakePictureCallback().onTakePictureDone(mOutputFile.getPath());
            }
        }
    }

    private static ITakePictureCallback iTakePictureCallback;

    public interface ITakePictureCallback {
        void onTakePictureDone(String pictureLocalPath);
    }

}
