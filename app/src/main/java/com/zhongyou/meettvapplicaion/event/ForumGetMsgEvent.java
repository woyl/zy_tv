package com.zhongyou.meettvapplicaion.event;

import com.zhongyou.meettvapplicaion.entities.PageData;

/**
 * 获得讨论区分页、新数据事件
 *
 * @author Dongce
 * create time: 2018/11/19
 */
public class ForumGetMsgEvent {
    private PageData pageData;

    public ForumGetMsgEvent(PageData pageData) {
        this.pageData = pageData;
    }

    public PageData getPageData() {
        return pageData;
    }
}
