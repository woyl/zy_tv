package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 18-2-27.
 */

public class ExpostorOnlineStats implements Entity, Parcelable {

    private String id;
    private String userId;
    private String deviceId;
    private int onlineTimestamp;
    private int offlineTimestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public int getOnlineTimestamp() {
        return onlineTimestamp;
    }

    public void setOnlineTimestamp(int onlineTimestamp) {
        this.onlineTimestamp = onlineTimestamp;
    }

    public int getOfflineTimestamp() {
        return offlineTimestamp;
    }

    public void setOfflineTimestamp(int offlineTimestamp) {
        this.offlineTimestamp = offlineTimestamp;
    }

    @Override
    public String toString() {
        return "ExpostorOnlineStats{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", onlineTimestamp=" + onlineTimestamp +
                ", offlineTimestamp=" + offlineTimestamp +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExpostorOnlineStats that = (ExpostorOnlineStats) o;

        if (onlineTimestamp != that.onlineTimestamp) return false;
        if (offlineTimestamp != that.offlineTimestamp) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (userId != null ? !userId.equals(that.userId) : that.userId != null) return false;
        return deviceId != null ? deviceId.equals(that.deviceId) : that.deviceId == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        result = 31 * result + (deviceId != null ? deviceId.hashCode() : 0);
        result = 31 * result + onlineTimestamp;
        result = 31 * result + offlineTimestamp;
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.userId);
        dest.writeString(this.deviceId);
        dest.writeInt(this.onlineTimestamp);
        dest.writeInt(this.offlineTimestamp);
    }

    public ExpostorOnlineStats() {
    }

    protected ExpostorOnlineStats(Parcel in) {
        this.id = in.readString();
        this.userId = in.readString();
        this.deviceId = in.readString();
        this.onlineTimestamp = in.readInt();
        this.offlineTimestamp = in.readInt();
    }

    public static final Creator<ExpostorOnlineStats> CREATOR = new Creator<ExpostorOnlineStats>() {
        @Override
        public ExpostorOnlineStats createFromParcel(Parcel source) {
            return new ExpostorOnlineStats(source);
        }

        @Override
        public ExpostorOnlineStats[] newArray(int size) {
            return new ExpostorOnlineStats[size];
        }
    };
}
