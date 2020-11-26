package com.zhongyou.meettvapplicaion.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class PressedTextView extends TextView {

    public PressedTextView(Context context) {
        super(context);
    }

    public PressedTextView(Context context, AttributeSet attrs) {
    	super(context, attrs);
    }

    public PressedTextView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

      
        if(pressed) {
            getBackground().setAlpha(0xCC);
            invalidate();
        }
        else {
        	getBackground().setAlpha(0xFF);
            invalidate();
        }
    }
    
    @Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		
		if(enabled){
			getBackground().setAlpha(0xFF);
            invalidate();
		}else{
			getBackground().setAlpha(0x80);
            invalidate();
		}
	}
}
