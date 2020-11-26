package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Objects;

public class Material implements Parcelable {

    private String id;

    private String createDate;

    private String updateDate;

    private String userId;

    private String name;

    private int auditStatus;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private ArrayList<MeetingMaterialsPublish> meetingMaterialsPublishList;

    private ArrayList<ConvertFileList> convertFileList;

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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public ArrayList<MeetingMaterialsPublish> getMeetingMaterialsPublishList() {
        return meetingMaterialsPublishList;
    }

    public void setMeetingMaterialsPublishList(ArrayList<MeetingMaterialsPublish> meetingMaterialsPublishList) {
        this.meetingMaterialsPublishList = meetingMaterialsPublishList;
    }

    public ArrayList<ConvertFileList> getConvertFileLists() {
        return convertFileList;
    }

    public void setConvertFileLists(ArrayList<ConvertFileList> convertFileLists) {
        this.convertFileList = convertFileLists;
    }

    @Override
    public String toString() {
        return "Material{" +
                "id='" + id + '\'' +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                ", userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", auditStatus=" + auditStatus +
                ", meetingMaterialsPublishList=" + meetingMaterialsPublishList +
                ", convertFileList=" + convertFileList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return auditStatus == material.auditStatus &&
                Objects.equals(id, material.id) &&
                Objects.equals(createDate, material.createDate) &&
                Objects.equals(updateDate, material.updateDate) &&
                Objects.equals(userId, material.userId) &&
                Objects.equals(name, material.name) &&
                Objects.equals(meetingMaterialsPublishList, material.meetingMaterialsPublishList) &&
                Objects.equals(convertFileList,material.convertFileList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, createDate, updateDate, userId, name, auditStatus, meetingMaterialsPublishList,convertFileList);
    }

    public Material() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.createDate);
        dest.writeString(this.updateDate);
        dest.writeString(this.userId);
        dest.writeString(this.name);
        dest.writeInt(this.auditStatus);
        dest.writeString(this.type);
        dest.writeTypedList(this.meetingMaterialsPublishList);
        dest.writeTypedList(this.convertFileList);
    }

    protected Material(Parcel in) {
        this.id = in.readString();
        this.createDate = in.readString();
        this.updateDate = in.readString();
        this.userId = in.readString();
        this.name = in.readString();
        this.auditStatus = in.readInt();
        this.type = in.readString();
        this.meetingMaterialsPublishList = in.createTypedArrayList(MeetingMaterialsPublish.CREATOR);
        this.convertFileList = in.createTypedArrayList(ConvertFileList.CREATOR);
    }

    public static final Creator<Material> CREATOR = new Creator<Material>() {
        @Override
        public Material createFromParcel(Parcel source) {
            return new Material(source);
        }

        @Override
        public Material[] newArray(int size) {
            return new Material[size];
        }
    };
}
