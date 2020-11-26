package com.zhongyou.meettvapplicaion.entities.base;

import java.util.ArrayList;


/**data为数组的bean基类
 * Created by wufan on 2016/12/29.
 */

public class BaseArrayBean<T> extends BaseErrorBean {
    private ArrayList<T> data;

    public ArrayList<T> getData() {
        return data;
    }

    public void setData(ArrayList<T> data) {
        this.data = data;
    }

    public boolean hasData(){
        return data!=null && data.size()!=0;
    }
}
