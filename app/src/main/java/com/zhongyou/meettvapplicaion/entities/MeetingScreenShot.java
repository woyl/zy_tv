package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * App 会议模块 - 参会人摄像头抓拍
 *
 * @author Dongce
 * create time: 2018/12/6
 */
public class MeetingScreenShot implements Parcelable, Entity {

    private String id;
    private String remarks;
    private String createDate;
    private String updateDate;
    private String delFlag;
    private String meetingId; //会议ID
    private String userId; //用户ID
    private String imgUrl; //抓拍后七牛url地址
    private long ts; //上传的毫秒时间戳

    protected MeetingScreenShot(Parcel in) {
        id = in.readString();
        remarks = in.readString();
        createDate = in.readString();
        updateDate = in.readString();
        delFlag = in.readString();
        meetingId = in.readString();
        userId = in.readString();
        imgUrl = in.readString();
        ts = in.readLong();
    }

    public static final Creator<MeetingScreenShot> CREATOR = new Creator<MeetingScreenShot>() {
        @Override
        public MeetingScreenShot createFromParcel(Parcel in) {
            return new MeetingScreenShot(in);
        }

        @Override
        public MeetingScreenShot[] newArray(int size) {
            return new MeetingScreenShot[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(remarks);
        dest.writeString(createDate);
        dest.writeString(updateDate);
        dest.writeString(delFlag);
        dest.writeString(meetingId);
        dest.writeString(userId);
        dest.writeString(imgUrl);
        dest.writeLong(ts);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
