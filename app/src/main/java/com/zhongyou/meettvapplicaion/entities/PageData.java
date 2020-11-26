package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dongce
 * create time: 2018/11/20
 */
public class PageData implements Parcelable, Entity {


    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;

    public static final int MSGTYPE_NORMAL = 0;//正常内容
    public static final int MSGTYPE_WITHDRAW = 1;//撤回消息
    public static final int MSGTYPE_AGGREGATION = 2;//聚合内容

    private String id;
    private String meetingId;
    private String title;
    private String replyTime;
    private String userLogo;
    private String userName;
    private String userId;
    /**
     * 内容类型：0是文本；1是图片
     */
    private int type;
    /**
     * 内容文字：如果type=0，content为文本内容；如果tpye=1，content为图片地址
     */
    private String content;
    private String atailUserId;
    private String atailUserName;
    private long replyTimestamp;
    /**
     * 消息类型：0：正常内容；1：撤回内容；2：聚合内容
     */
    private int msgType;

    protected PageData(Parcel in) {
        id = in.readString();
        meetingId = in.readString();
        title = in.readString();
        replyTime = in.readString();
        userLogo = in.readString();
        userName = in.readString();
        userId = in.readString();
        type = in.readInt();
        content = in.readString();
        atailUserId = in.readString();
        atailUserName = in.readString();
        replyTimestamp = in.readLong();
        msgType = in.readInt();
    }

    public static final Creator<PageData> CREATOR = new Creator<PageData>() {
        @Override
        public PageData createFromParcel(Parcel in) {
            return new PageData(in);
        }

        @Override
        public PageData[] newArray(int size) {
            return new PageData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(meetingId);
        dest.writeString(title);
        dest.writeString(replyTime);
        dest.writeString(userLogo);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeInt(type);
        dest.writeString(content);
        dest.writeString(atailUserId);
        dest.writeString(atailUserName);
        dest.writeLong(replyTimestamp);
        dest.writeInt(msgType);
    }

    @Override
    public String toString() {
        return "PageData{" +
                "id='" + id + '\'' +
                ", meetingId='" + meetingId + '\'' +
                ", title='" + title + '\'' +
                ", replyTime='" + replyTime + '\'' +
                ", userLogo='" + userLogo + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", atailUserId='" + atailUserId + '\'' +
                ", atailUserName='" + atailUserName + '\'' +
                ", replyTimestamp=" + replyTimestamp +
                ", msgType=" + msgType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PageData pageData = (PageData) o;

        if (id != null ? !id.equals(pageData.id) : pageData.id != null)
            return false;
        if (meetingId != null ? !meetingId.equals(pageData.meetingId) : pageData.meetingId != null)
            return false;
        if (title != null ? !title.equals(pageData.title) : pageData.title != null)
            return false;
        if (replyTime != null ? !replyTime.equals(pageData.replyTime) : pageData.replyTime != null)
            return false;
        if (userLogo != null ? !userLogo.equals(pageData.userLogo) : pageData.userLogo != null)
            return false;
        if (userName != null ? !userName.equals(pageData.userName) : pageData.userName != null)
            return false;
        if (userId != null ? !userId.equals(pageData.userId) : pageData.userId != null)
            return false;
        if (type != pageData.type) return false;
        if (content != null ? !content.equals(pageData.content) : pageData.content != null)
            return false;
        if (atailUserId != null ? !atailUserId.equals(pageData.atailUserId) : pageData.atailUserId != null)
            return false;
        if (atailUserName != null ? !atailUserName.equals(pageData.atailUserName) : pageData.atailUserName != null)
            return false;
        if (replyTimestamp != pageData.replyTimestamp) return false;
        return msgType != pageData.msgType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getUserLogo() {
        return userLogo;
    }

    public void setUserLogo(String userLogo) {
        this.userLogo = userLogo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAtailUserId() {
        return atailUserId;
    }

    public void setAtailUserId(String atailUserId) {
        this.atailUserId = atailUserId;
    }

    public String getAtailUserName() {
        return atailUserName;
    }

    public void setAtailUserName(String atailUserName) {
        this.atailUserName = atailUserName;
    }

    public long getReplyTimestamp() {
        return replyTimestamp;
    }

    public void setReplyTimestamp(long replyTimestamp) {
        this.replyTimestamp = replyTimestamp;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }
}
