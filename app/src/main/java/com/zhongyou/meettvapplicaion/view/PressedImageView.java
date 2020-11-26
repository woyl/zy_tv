package com.zhongyou.meettvapplicaion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PressedImageView extends ImageView {

    public PressedImageView(Context context) {
        super(context);
    }

    public PressedImageView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }

    public PressedImageView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if(getDrawable() == null)
            return;

        if(pressed) {
//            getDrawable().setColorFilter(0x44000000, android.graphics.PorterDuff.Mode.SRC_ATOP);
            setImageAlpha(0x80);
            invalidate();
        }
        else {
//            getDrawable().clearColorFilter();
            setImageAlpha(0xFF);
            invalidate();
        }
    }
    
    @Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if(getDrawable() == null)
            return;
		
		if(enabled){
			setImageAlpha(0xFF);
            invalidate();
		}else{
			setImageAlpha(0x80);
            invalidate();
		}
	}
}
