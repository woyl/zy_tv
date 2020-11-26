package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class MeetingMaterialsPublish implements Parcelable {

    private String id;

    private String url;

    private String name;

    private String materialsId;

    private int priority;

    private String createDate;

    private String updateDate;

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterialsId() {
        return materialsId;
    }

    public void setMaterialsId(String materialsId) {
        this.materialsId = materialsId;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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

    @Override
    public String toString() {
        return "MeetingMaterialsPublish{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", materialsId='" + materialsId + '\'' +
                ", priority=" + priority +
                ", createDate='" + createDate + '\'' +
                ", updateDate='" + updateDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeetingMaterialsPublish that = (MeetingMaterialsPublish) o;
        return priority == that.priority &&
                Objects.equals(id, that.id) &&
                Objects.equals(url, that.url) &&
                Objects.equals(name, that.name) &&
                Objects.equals(materialsId, that.materialsId) &&
                Objects.equals(createDate, that.createDate) &&
                Objects.equals(updateDate, that.updateDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, url, name, materialsId, priority, createDate, updateDate);
    }

    public MeetingMaterialsPublish() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.url);
        dest.writeString(this.name);
        dest.writeString(this.materialsId);
        dest.writeInt(this.priority);
        dest.writeString(this.createDate);
        dest.writeString(this.updateDate);
        dest.writeString(this.type);
    }

    protected MeetingMaterialsPublish(Parcel in) {
        this.id = in.readString();
        this.url = in.readString();
        this.name = in.readString();
        this.materialsId = in.readString();
        this.priority = in.readInt();
        this.createDate = in.readString();
        this.updateDate = in.readString();
        this.type = in.readString();
    }

    public static final Creator<MeetingMaterialsPublish> CREATOR = new Creator<MeetingMaterialsPublish>() {
        @Override
        public MeetingMaterialsPublish createFromParcel(Parcel source) {
            return new MeetingMaterialsPublish(source);
        }

        @Override
        public MeetingMaterialsPublish[] newArray(int size) {
            return new MeetingMaterialsPublish[size];
        }
    };
}
