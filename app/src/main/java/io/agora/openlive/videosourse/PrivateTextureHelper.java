package io.agora.openlive.videosourse;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.Log;
import android.view.TextureView;

import java.nio.ByteBuffer;

import io.agora.rtc.gl.EglBase;
import io.agora.rtc.gl.RendererCommon;
import io.agora.rtc.mediaio.BaseVideoRenderer;
import io.agora.rtc.mediaio.IVideoSink;
import io.agora.rtc.mediaio.MediaIO;
import io.agora.rtc.video.AgoraVideoFrame;

/**
 * Created by keke on 2017/12/29.
 */

public class PrivateTextureHelper implements IVideoSink, TextureView.SurfaceTextureListener {
    private final static String TAG = TextureView.class.getSimpleName();

    private BaseVideoRenderer mRender;

    private EglBase.Context mEglContext;
    private int[] mConfigAttributes;
    private RendererCommon.GlDrawer mDrawer;

    private TextureView mTextureView;

    private TextureListener textureListener;

    public interface TextureListener {
        void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height);
        void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height);
        boolean onSurfaceTextureDestroyed(SurfaceTexture surface);
        void onSurfaceTextureUpdated(SurfaceTexture surface);
    }

    public void setTextureViewListener (TextureListener textureListener) {
        this.textureListener = textureListener;
    }

    public PrivateTextureHelper(Context context, TextureView view) {
        mTextureView = view;
        mRender = new BaseVideoRenderer(TAG);
        mRender.setRenderView(mTextureView, this);
    }

    public void init(EglBase.Context sharedContext) {
        mEglContext = sharedContext;
    }

    public void init(final EglBase.Context sharedContext, final int[] configAttributes, RendererCommon.GlDrawer drawer) {
        mEglContext = sharedContext;
        mConfigAttributes = configAttributes;
        mDrawer = drawer;
    }

    public void setBufferType(MediaIO.BufferType bufferType) {
        mRender.setBufferType(bufferType);
    }

    public void setPixelFormat(MediaIO.PixelFormat pixelFormat) {
        mRender.setPixelFormat(pixelFormat);
    }

    public void setMirror(final boolean mirror) {
        mRender.getEglRender().setMirror(mirror);
    }

    // from IVideoRenderer begin
    @Override
    public boolean onInitialize() {
        Log.i(TAG, "onInitialize");
        if (mConfigAttributes != null && mDrawer != null) {
            mRender.init(mEglContext, mConfigAttributes, mDrawer);
        } else {
            mRender.init(mEglContext);
        }
        return true;
    }

    @Override
    public boolean onStart() {
        Log.i(TAG, "onStart");
        return mRender.start();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        mRender.stop();
    }

    @Override
    public void onDispose() {
        Log.i(TAG, "onDispose");
        mRender.release();
    }

    @Override
    public long getEGLContextHandle() {
        return mRender.getEGLContextHandle();
    }

    @Override
    public int getPixelFormat() {
        int format = mRender.getPixelFormat();
        if (format == AgoraVideoFrame.FORMAT_NONE) {
            throw new IllegalArgumentException("Pixel format is not set");
        }
        return format;
    }

    @Override
    public int getBufferType() {
        int type = mRender.getBufferType();
        if (type == AgoraVideoFrame.BUFFER_TYPE_NONE) {
            throw new IllegalArgumentException("Buffer type is not set");
        }
        return type;
    }

    @Override
    public void consumeTextureFrame(int texId, int format, int width, int height, int rotation, long ts, float[] matrix) {
        Log.d(TAG, "consumeTextureFrame");
        mRender.consume(texId, format, width, height, 0, ts, matrix);
    }

    @Override
    public void consumeByteBufferFrame(ByteBuffer buffer, int format, int width, int height, int rotation, long ts) {
        Log.d(TAG, "consumeByteBufferFrame");
        mRender.consume(buffer, format, width, height, rotation, ts);
    }

    @Override
    public void consumeByteArrayFrame(byte[] data, int format, int width, int height, int rotation, long ts) {
        Log.d(TAG, "consumeByteArrayFrame");
        mRender.consume(data, format, width, height, rotation, ts);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureAvailable");
        if (textureListener != null) {
            textureListener.onSurfaceTextureAvailable(surface, width, height);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        Log.d(TAG, "onSurfaceTextureSizeChanged");
        if (textureListener != null) {
            textureListener.onSurfaceTextureSizeChanged(surface, width, height);
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureDestroyed");
        if (textureListener != null) {
            textureListener.onSurfaceTextureDestroyed(surface);
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.d(TAG, "onSurfaceTextureUpdated");
        if (textureListener != null) {
            textureListener.onSurfaceTextureUpdated(surface);
        }
    }

}
