package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

/**
 * Created by yuna on 2017/7/27.
 */

public class Audience implements Entity, Parcelable {

    private int uid;

    private String uname;

    private String postTypeName;

    private int auditStatus; // 1.已认证

    private int callStatus; // 0-未通话，1-连接中，2-正在通话

    private boolean handsUp; // true-申请发言，false-未申请发言

    public int getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(int auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getPostTypeName() {
        return postTypeName;
    }

    public void setPostTypeName(String postTypeName) {
        this.postTypeName = postTypeName;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public boolean isHandsUp() {
        return handsUp;
    }

    public void setHandsUp(boolean handsUp) {
        this.handsUp = handsUp;
    }

    public int getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(int callStatus) {
        this.callStatus = callStatus;
    }

    //    @Override
//    public String toString() {
//        return "Audience{" +
//                "uid='" + uid + '\'' +
//                ", uname='" + uname + '\'' +
//                '}';
//    }

    @Override
    public String toString() {
        return uname;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Audience audience = (Audience) o;
        return uid == audience.uid &&
                auditStatus == audience.auditStatus &&
                handsUp == audience.handsUp &&
                callStatus == audience.callStatus &&
                Objects.equals(uname, audience.uname) &&
                Objects.equals(postTypeName, audience.postTypeName);
    }

    @Override
    public int hashCode() {

        return Objects.hash(uid, uname, auditStatus, postTypeName, handsUp, callStatus);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.uid);
        dest.writeString(this.uname);
        dest.writeInt(this.auditStatus);
        dest.writeString(this.postTypeName);
        dest.writeByte(this.handsUp ? (byte) 1 : (byte) 0);
        dest.writeInt(this.callStatus);
    }

    public Audience() {
    }

    protected Audience(Parcel in) {
        this.uid = in.readInt();
        this.uname = in.readString();
        this.auditStatus = in.readInt();
        this.postTypeName = in.readString();
        this.handsUp = in.readByte() != 0;
        this.callStatus = in.readInt();
    }

    public static final Creator<Audience> CREATOR = new Creator<Audience>() {
        @Override
        public Audience createFromParcel(Parcel source) {
            return new Audience(source);
        }

        @Override
        public Audience[] newArray(int size) {
            return new Audience[size];
        }
    };
}
