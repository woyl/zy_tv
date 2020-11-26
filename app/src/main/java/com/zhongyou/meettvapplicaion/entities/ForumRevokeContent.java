package com.zhongyou.meettvapplicaion.entities;

/**
 * 讨论区撤回消息
 *
 * @author Dongce
 * create time: 2018/12/3
 */
public class ForumRevokeContent {
    private String userName;

    private String contentId;

    private String meetingId;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }
}
