package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dongce
 * create time: 2018/11/24
 */
public class ForumComment implements Parcelable, Entity {
    private String id;
    private String createDate;
    private String updateDate;
    private String delFlag;
    private String meetingId;
    private String userId;
    private int type;
    private String content;
    private long ts;


    protected ForumComment(Parcel in) {
        id = in.readString();
        createDate = in.readString();
        updateDate = in.readString();
        delFlag = in.readString();
        meetingId = in.readString();
        userId = in.readString();
        type = in.readInt();
        content = in.readString();
        ts = in.readLong();
    }

    public static final Creator<ForumComment> CREATOR = new Creator<ForumComment>() {
        @Override
        public ForumComment createFromParcel(Parcel in) {
            return new ForumComment(in);
        }

        @Override
        public ForumComment[] newArray(int size) {
            return new ForumComment[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(createDate);
        dest.writeString(updateDate);
        dest.writeString(delFlag);
        dest.writeString(meetingId);
        dest.writeString(userId);
        dest.writeInt(type);
        dest.writeString(content);
        dest.writeLong(ts);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
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

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
