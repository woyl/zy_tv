package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class Agora implements Parcelable, Entity {

    private String isTest;

    private String appID;

    private String token;

    private String signalingKey;

    public String getIsTest() {
        return isTest;
    }

    public void setIsTest(String isTest) {
        this.isTest = isTest;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSignalingKey() {
        return signalingKey;
    }

    public void setSignalingKey(String signalingKey) {
        this.signalingKey = signalingKey;
    }

    @Override
    public String toString() {
        return "Agora{" +
                "isTest='" + isTest + '\'' +
                ", appID='" + appID + '\'' +
                ", token='" + token + '\'' +
                ", signalingKey='" + signalingKey + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agora agora = (Agora) o;
        return Objects.equals(isTest, agora.isTest) &&
                Objects.equals(appID, agora.appID) &&
                Objects.equals(token, agora.token) &&
                Objects.equals(signalingKey, agora.signalingKey);
    }

    @Override
    public int hashCode() {

        return Objects.hash(isTest, appID, token, signalingKey);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.isTest);
        dest.writeString(this.appID);
        dest.writeString(this.token);
        dest.writeString(this.signalingKey);
    }

    public Agora() {
    }

    protected Agora(Parcel in) {
        this.isTest = in.readString();
        this.appID = in.readString();
        this.token = in.readString();
        this.signalingKey = in.readString();
    }

    public static final Creator<Agora> CREATOR = new Creator<Agora>() {
        @Override
        public Agora createFromParcel(Parcel source) {
            return new Agora(source);
        }

        @Override
        public Agora[] newArray(int size) {
            return new Agora[size];
        }
    };
}
