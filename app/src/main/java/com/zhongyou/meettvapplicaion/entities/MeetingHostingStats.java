package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 18-2-28.
 */

public class MeetingHostingStats implements Entity, Parcelable {

    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "MeetingHostingStats{" +
                "id='" + id + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingHostingStats that = (MeetingHostingStats) o;

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

    public MeetingHostingStats() {
    }

    protected MeetingHostingStats(Parcel in) {
        this.id = in.readString();
    }

    public static final Creator<MeetingHostingStats> CREATOR = new Creator<MeetingHostingStats>() {
        @Override
        public MeetingHostingStats createFromParcel(Parcel source) {
            return new MeetingHostingStats(source);
        }

        @Override
        public MeetingHostingStats[] newArray(int size) {
            return new MeetingHostingStats[size];
        }
    };
}
