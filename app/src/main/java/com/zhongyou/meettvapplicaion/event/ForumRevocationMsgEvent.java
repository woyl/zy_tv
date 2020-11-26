package com.zhongyou.meettvapplicaion.event;

import com.zhongyou.meettvapplicaion.entities.ForumRevokeContent;

/**
 * 获得讨论区撤销数据事件
 *
 * @author Dongce
 * create time: 2018/11/19
 */
public class ForumRevocationMsgEvent {
    private ForumRevokeContent forumRevokeContent;

    public ForumRevocationMsgEvent(ForumRevokeContent forumRevokeContent) {
        this.forumRevokeContent = forumRevokeContent;
    }

    public ForumRevokeContent getForumRevokeContent() {
        return forumRevokeContent;
    }
}
