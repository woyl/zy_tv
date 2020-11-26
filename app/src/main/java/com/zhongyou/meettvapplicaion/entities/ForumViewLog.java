package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 讨论区已读
 *
 * @author Dongce
 * create time: 2018/11/30
 */
public class ForumViewLog implements Parcelable, Entity {
    private String id;
    private String createDate;
    private String updateDate;
    private String delFlag;
    private String meetingId;
    private String userId;
    private String deviceId;


    protected ForumViewLog(Parcel in) {
        id = in.readString();
        createDate = in.readString();
        updateDate = in.readString();
        delFlag = in.readString();
        meetingId = in.readString();
        userId = in.readString();
        deviceId = in.readString();
    }

    public static final Creator<ForumViewLog> CREATOR = new Creator<ForumViewLog>() {
        @Override
        public ForumViewLog createFromParcel(Parcel in) {
            return new ForumViewLog(in);
        }

        @Override
        public ForumViewLog[] newArray(int size) {
            return new ForumViewLog[size];
        }
    };

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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

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
        dest.writeString(deviceId);
    }
}
