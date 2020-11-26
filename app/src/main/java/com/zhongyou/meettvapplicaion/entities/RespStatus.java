package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by whatisjava on 15/11/11.
 */
public class RespStatus implements Parcelable {

    private int errcode;

    private String errmsg;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RespStatus that = (RespStatus) o;

        if (errcode != that.errcode) return false;
        return errmsg != null ? errmsg.equals(that.errmsg) : that.errmsg == null;

    }

    @Override
    public int hashCode() {
        int result = errcode;
        result = 31 * result + (errmsg != null ? errmsg.hashCode() : 0);
        return result;
    }

    public int getErrcode() {

        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        return "RespStatus{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.errcode);
        dest.writeString(this.errmsg);
    }

    public RespStatus() {
    }

    protected RespStatus(Parcel in) {
        this.errcode = in.readInt();
        this.errmsg = in.readString();
    }

    public static final Creator<RespStatus> CREATOR = new Creator<RespStatus>() {
        @Override
        public RespStatus createFromParcel(Parcel source) {
            return new RespStatus(source);
        }

        @Override
        public RespStatus[] newArray(int size) {
            return new RespStatus[size];
        }
    };
}
