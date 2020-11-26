package com.zhongyou.meettvapplicaion.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

/**
 * Created by whatisjava on 16/5/18.
 */
public class RoundCornerTransform implements Transformation {

    private final int radius;
    private final int margin;  // dp
    private HalfType halfType;

    // radius is corner radiu in dp
    // margin is the board in dp
    public RoundCornerTransform(final int radius, final int margin, HalfType halfType) {
        this.radius = radius;
        this.margin = margin;
        this.halfType = halfType;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        int widthLight = source.getWidth();
        int heightLight = source.getHeight();

        Bitmap output = Bitmap.createBitmap(widthLight, heightLight, Bitmap.Config.ARGB_8888);//创建一个和原始图片一样大小的位图
        Canvas canvas = new Canvas(output);//创建位图画布
        Paint paint = new Paint();//创建画笔
        paint.setAntiAlias(true);// 抗锯齿
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

        Rect rect = new Rect(margin, margin, widthLight - margin, heightLight - margin);//创建一个和原始图片一样大小的矩形
        RectF rectF = new RectF(rect);
        canvas.drawRoundRect(rectF, radius, radius, paint);//画一个基于前面创建的矩形大小的圆角矩形

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));//设置相交模式
        canvas.drawBitmap(source, 0, 0, paintImage);//把图片画到矩形去
        source.recycle();
        switch (halfType) {
            case LEFT:
                return Bitmap.createBitmap(output, 0, 0, widthLight - radius, heightLight);
            case RIGHT:
                return Bitmap.createBitmap(output, widthLight - radius, 0, widthLight - radius, heightLight);
            case TOP: // 上半部分圆角化 “- roundPixels”实际上为了保证底部没有圆角，采用截掉一部分的方式，就是截掉和弧度一样大小的长度
                return Bitmap.createBitmap(output, 0, 0, widthLight, heightLight - radius);
            case BOTTOM:
                return Bitmap.createBitmap(output, 0, heightLight - radius, widthLight, heightLight - radius);
            case ALL:
                return output;
            default:
                return output;
        }
    }

    @Override
    public String key() {
        return "rounded";
    }
}
