package com.zhongyou.meettvapplicaion.view;

/**
 *  按键加载更多接口.
 * Created by hailongqiu on 2016/9/5.
 */
public interface PrvInterface {

    void setOnLoadMoreComplete(); // 按键加载更多-->完成.
    void setOnLoadMoreUpComplete();
    void setPagingableListener(RecyclerViewTV.PagingableListener pagingableListener);
    void setPagingableUpListener(RecyclerViewTV.PagingableUpListener pagingableListener);

}
