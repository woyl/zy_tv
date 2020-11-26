package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 18-2-27.
 */

public class MeetingJoinStats implements Entity, Parcelable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MeetingJoinStats{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingJoinStats that = (MeetingJoinStats) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
    }

    public MeetingJoinStats() {
    }

    protected MeetingJoinStats(Parcel in) {
        this.id = in.readString();
    }

    public static final Creator<MeetingJoinStats> CREATOR = new Creator<MeetingJoinStats>() {
        @Override
        public MeetingJoinStats createFromParcel(Parcel source) {
            return new MeetingJoinStats(source);
        }

        @Override
        public MeetingJoinStats[] newArray(int size) {
            return new MeetingJoinStats[size];
        }
    };
}
