package com.zhongyou.meettvapplicaion.business.adapter;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zhongyou.meettvapplicaion.R;
import com.zhongyou.meettvapplicaion.business.StatusesActivity;
import com.zhongyou.meettvapplicaion.entities.StatusType;
import com.zhongyou.meettvapplicaion.view.GeneralAdapter;
import com.zhongyou.meettvapplicaion.view.OpenPresenter;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by whatisjava on 17-10-18.
 */

public class LabelAdapter extends OpenPresenter {

    private Context mContext;
    private ArrayList<StatusType> statusTypes;
    private GeneralAdapter mAdapter;

    private boolean isFinish = false;



    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public LabelAdapter(Context context, ArrayList<StatusType> statusTypes) {
        this.mContext = context;
        this.statusTypes = statusTypes;

    }


    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }

    @Override
    public int getItemCount() {
        return statusTypes.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_status_type_label, parent, false);

        return new LabelHodler(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        ((LabelHodler)viewHolder).labelButton.setText(statusTypes.get(position).getName());


            ((LabelHodler) viewHolder).view.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    Logger.e("1000  当前焦点改变的position是："+position);
                    if (mOnFocusChangeListener!=null){
                        mOnFocusChangeListener.onFocusChange(position, (LabelHodler) viewHolder,hasFocus);
                    }
                }
            });

        viewHolder.view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                Log.e("LabelAdapter", "onKey: "+keyCode );

                if (keyCode==KeyEvent.KEYCODE_DPAD_DOWN){

                    ((StatusesActivity)mContext).setNewsFocus();

                }else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT){
                    if (position==statusTypes.size()-1){
                        return true;
                    }else {
                        return false;
                    }
                }
                return false;
            }
        });

    }



    public interface  onFocusChangeListener{
       void onFocusChange(int posiont, LabelHodler viewHolder,boolean hasFocus);
    }


    private onFocusChangeListener mOnFocusChangeListener;
    public void setOnFocusChangeListener(onFocusChangeListener onFocusChangeListener){
        this.mOnFocusChangeListener=onFocusChangeListener;
    }




    public class LabelHodler extends OpenPresenter.ViewHolder {

       public  TextView labelButton;
        public   TextView mSliderView;

        public LabelHodler(View itemView) {
            super(itemView);

            labelButton = itemView.findViewById(R.id.label);
            mSliderView=itemView.findViewById(R.id.sideView);

        }
    }

}
