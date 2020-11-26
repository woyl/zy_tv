package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by yuna on 2017/7/27.
 */

public class MeetingJoin implements Entity, Parcelable {

    private int role; // 0-主播； 1-观众

    private Meeting meeting;

    private HostUser hostUser;

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public HostUser getHostUser() {
        return hostUser;
    }

    public void setHostUser(HostUser hostUser) {
        this.hostUser = hostUser;
    }

    @Override
    public String toString() {
        return "MeetingJoin{" +
                "role=" + role +
                ", meeting=" + meeting +
                ", hostUser=" + hostUser +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MeetingJoin that = (MeetingJoin) o;

        if (role != that.role) return false;
        if (meeting != null ? !meeting.equals(that.meeting) : that.meeting != null) return false;
        return hostUser != null ? hostUser.equals(that.hostUser) : that.hostUser == null;
    }

    @Override
    public int hashCode() {
        int result = role;
        result = 31 * result + (meeting != null ? meeting.hashCode() : 0);
        result = 31 * result + (hostUser != null ? hostUser.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.role);
        dest.writeParcelable(this.meeting, flags);
        dest.writeParcelable(this.hostUser, flags);
    }

    public MeetingJoin() {
    }

    protected MeetingJoin(Parcel in) {
        this.role = in.readInt();
        this.meeting = in.readParcelable(Meeting.class.getClassLoader());
        this.hostUser = in.readParcelable(HostUser.class.getClassLoader());
    }

    public static final Creator<MeetingJoin> CREATOR = new Creator<MeetingJoin>() {
        @Override
        public MeetingJoin createFromParcel(Parcel source) {
            return new MeetingJoin(source);
        }

        @Override
        public MeetingJoin[] newArray(int size) {
            return new MeetingJoin[size];
        }
    };
}
