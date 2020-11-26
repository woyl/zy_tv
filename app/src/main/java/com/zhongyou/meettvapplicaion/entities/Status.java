package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by yuna on 2017/7/27.
 */

public class Status implements Entity, Parcelable{

    private String id;

    private String typeId;

    private String userId;

    private String status;

    private int auditStatus;

    private int likeNum;

    private int viewingNum;

    private int type; // 0为图片，1为视频

    private String userLogo;

    private String userName;

    private String publishTime;

    private String publishDate;

    private String publishMinute;

    private ArrayList<SpaceStatusPublish> spaceStatusPublishList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getViewingNum() {
        return viewingNum;
    }

    public void setViewingNum(int viewingNum) {
        this.viewingNum = viewingNum;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getPublishMinute() {
        return publishMinute;
    }

    public void setPublishMinute(String publishMinute) {
        this.publishMinute = publishMinute;
    }

    public ArrayList<SpaceStatusPublish> getSpaceStatusPublishList() {
        return spaceStatusPublishList;
    }

    public void setSpaceStatusPublishList(ArrayList<SpaceStatusPublish> spaceStatusPublishList) {
        this.spaceStatusPublishList = spaceStatusPublishList;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    @Override
    public String toString() {
        return "Status{" +
                "id='" + id + '\'' +
                ", typeId='" + typeId + '\'' +
                ", userId='" + userId + '\'' +
                ", status='" + status + '\'' +
                ", auditStatus=" + auditStatus +
                ", likeNum=" + likeNum +
                ", viewingNum=" + viewingNum +
                ", type=" + type +
                ", userLogo='" + userLogo + '\'' +
                ", userName='" + userName + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", publishMinute='" + publishMinute + '\'' +
                ", spaceStatusPublishList=" + spaceStatusPublishList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Status status1 = (Status) o;
        return auditStatus == status1.auditStatus &&
                likeNum == status1.likeNum &&
                viewingNum == status1.viewingNum &&
                type == status1.type &&
                Objects.equals(id, status1.id) &&
                Objects.equals(typeId, status1.typeId) &&
                Objects.equals(userId, status1.userId) &&
                Objects.equals(status, status1.status) &&
                Objects.equals(userLogo, status1.userLogo) &&
                Objects.equals(userName, status1.userName) &&
                Objects.equals(publishTime, status1.publishTime) &&
                Objects.equals(publishDate, status1.publishDate) &&
                Objects.equals(publishMinute, status1.publishMinute) &&
                Objects.equals(spaceStatusPublishList, status1.spaceStatusPublishList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, typeId, userId, status, auditStatus, likeNum, viewingNum, type, userLogo, userName, publishTime, publishDate, publishMinute, spaceStatusPublishList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.typeId);
        dest.writeString(this.userId);
        dest.writeString(this.status);
        dest.writeInt(this.auditStatus);
        dest.writeInt(this.likeNum);
        dest.writeInt(this.viewingNum);
        dest.writeInt(this.type);
        dest.writeString(this.userLogo);
        dest.writeString(this.userName);
        dest.writeString(this.publishTime);
        dest.writeString(this.publishDate);
        dest.writeString(this.publishMinute);
        dest.writeTypedList(this.spaceStatusPublishList);
    }

    public Status() {
    }

    protected Status(Parcel in) {
        this.id = in.readString();
        this.typeId = in.readString();
        this.userId = in.readString();
        this.status = in.readString();
        this.auditStatus = in.readInt();
        this.likeNum = in.readInt();
        this.viewingNum = in.readInt();
        this.type = in.readInt();
        this.userLogo = in.readString();
        this.userName = in.readString();
        this.publishTime = in.readString();
        this.publishDate = in.readString();
        this.publishMinute = in.readString();
        this.spaceStatusPublishList = in.createTypedArrayList(SpaceStatusPublish.CREATOR);
    }

    public static final Creator<Status> CREATOR = new Creator<Status>() {
        @Override
        public Status createFromParcel(Parcel source) {
            return new Status(source);
        }

        @Override
        public Status[] newArray(int size) {
            return new Status[size];
        }
    };
}
