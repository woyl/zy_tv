package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 获取该用户是否是会议管理员
 *
 * @author Dongce
 * create time: 2018/12/12
 */
public class MeeetingAdmin implements Parcelable, Entity {

    private String meetingMgrUrl;//会议管理webview的url
    private boolean meetingAdmin;//是否是admin


    protected MeeetingAdmin(Parcel in) {
        meetingMgrUrl = in.readString();
        meetingAdmin = in.readByte() != 0;
    }

    public static final Creator<MeeetingAdmin> CREATOR = new Creator<MeeetingAdmin>() {
        @Override
        public MeeetingAdmin createFromParcel(Parcel in) {
            return new MeeetingAdmin(in);
        }

        @Override
        public MeeetingAdmin[] newArray(int size) {
            return new MeeetingAdmin[size];
        }
    };

    public String getMeetingMgrUrl() {
        return meetingMgrUrl;
    }

    public void setMeetingMgrUrl(String meetingMgrUrl) {
        this.meetingMgrUrl = meetingMgrUrl;
    }

    public boolean isMeetingAdmin() {
        return meetingAdmin;
    }

    public void setMeetingAdmin(boolean meetingAdmin) {
        this.meetingAdmin = meetingAdmin;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(meetingMgrUrl);
        dest.writeByte((byte) (meetingAdmin ? 1 : 0));
    }
}
