package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Liked implements Entity, Parcelable {

    private String id;

    private String statusId;

    private String userId;

    private String createDate;

    @Override
    public String toString() {
        return "Liked{" +
                "id='" + id + '\'' +
                ", statusId='" + statusId + '\'' +
                ", userId='" + userId + '\'' +
                ", createDate='" + createDate + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Liked liked = (Liked) o;
        return Objects.equals(id, liked.id) &&
                Objects.equals(statusId, liked.statusId) &&
                Objects.equals(userId, liked.userId) &&
                Objects.equals(createDate, liked.createDate);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, statusId, userId, createDate);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.statusId);
        dest.writeString(this.userId);
        dest.writeString(this.createDate);
    }

    public Liked() {
    }

    protected Liked(Parcel in) {
        this.id = in.readString();
        this.statusId = in.readString();
        this.userId = in.readString();
        this.createDate = in.readString();
    }

    public static final Creator<Liked> CREATOR = new Creator<Liked>() {
        @Override
        public Liked createFromParcel(Parcel source) {
            return new Liked(source);
        }

        @Override
        public Liked[] newArray(int size) {
            return new Liked[size];
        }
    };
}
