package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class SpaceStatusPublish implements Entity, Parcelable{

    private String id;
    private String statusId;
    private String url;
    private String urlLow;
    private int type;
    private int videoDuration;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlLow() {
        return urlLow;
    }

    public void setUrlLow(String urlLow) {
        this.urlLow = urlLow;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    @Override
    public String toString() {
        return "SpaceStatusPublish{" +
                "id='" + id + '\'' +
                ", statusId='" + statusId + '\'' +
                ", url='" + url + '\'' +
                ", urlLow='" + urlLow + '\'' +
                ", type=" + type +
                ", videoDuration=" + videoDuration +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SpaceStatusPublish that = (SpaceStatusPublish) o;
        return type == that.type &&
                videoDuration == that.videoDuration &&
                Objects.equals(id, that.id) &&
                Objects.equals(statusId, that.statusId) &&
                Objects.equals(url, that.url) &&
                Objects.equals(urlLow, that.urlLow);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, statusId, url, urlLow, type, videoDuration);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.statusId);
        dest.writeString(this.url);
        dest.writeString(this.urlLow);
        dest.writeInt(this.type);
        dest.writeInt(this.videoDuration);
    }

    public SpaceStatusPublish() {
    }

    protected SpaceStatusPublish(Parcel in) {
        this.id = in.readString();
        this.statusId = in.readString();
        this.url = in.readString();
        this.urlLow = in.readString();
        this.type = in.readInt();
        this.videoDuration = in.readInt();
    }

    public static final Creator<SpaceStatusPublish> CREATOR = new Creator<SpaceStatusPublish>() {
        @Override
        public SpaceStatusPublish createFromParcel(Parcel source) {
            return new SpaceStatusPublish(source);
        }

        @Override
        public SpaceStatusPublish[] newArray(int size) {
            return new SpaceStatusPublish[size];
        }
    };
}
