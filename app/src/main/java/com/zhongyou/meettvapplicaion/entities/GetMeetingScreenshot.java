package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Dongce
 * create time: 2018/12/7
 */
public class GetMeetingScreenshot implements Parcelable, Entity {
    private String id;//记录ID
    private String meetingId;//会议ID
    private String userId;//用户ID
    private String imgUrl;//截屏图片地址
    private String userName;//姓名
    private String areaName;//中心
    private String createDate;//抓拍时间
    private long ts;//上传的毫秒时间戳

    protected GetMeetingScreenshot(Parcel in) {
        id = in.readString();
        meetingId = in.readString();
        userId = in.readString();
        imgUrl = in.readString();
        userName = in.readString();
        areaName = in.readString();
        createDate = in.readString();
        ts = in.readLong();
    }

    public static final Creator<GetMeetingScreenshot> CREATOR = new Creator<GetMeetingScreenshot>() {
        @Override
        public GetMeetingScreenshot createFromParcel(Parcel in) {
            return new GetMeetingScreenshot(in);
        }

        @Override
        public GetMeetingScreenshot[] newArray(int size) {
            return new GetMeetingScreenshot[size];
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
        dest.writeString(userId);
        dest.writeString(imgUrl);
        dest.writeString(userName);
        dest.writeString(areaName);
        dest.writeString(createDate);
        dest.writeLong(ts);
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }
}
