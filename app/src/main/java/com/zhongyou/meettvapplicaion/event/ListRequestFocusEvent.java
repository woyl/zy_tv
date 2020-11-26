package com.zhongyou.meettvapplicaion.event;

/**
 * Created by yuna on 2017/7/4.
 */

public class ListRequestFocusEvent {

    public int status = 0;
    public ListRequestFocusEvent(int pos){
        this.status = pos;
    }
}
