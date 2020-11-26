package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class StatusType implements Parcelable, Entity {

    private String id;

    private String name;

    private int priority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    private boolean isFoucsed;

    private boolean getFoucsed() {
        return isFoucsed;
    }

    public void setFoucsed(boolean foucsed) {
        isFoucsed = foucsed;
    }

    @Override
    public String toString() {
        return "StatusType{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", priority=" + priority +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusType that = (StatusType) o;
        return priority == that.priority &&
                Objects.equals(id, that.id) &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, priority);
    }

    public StatusType() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.priority);
        dest.writeByte(this.isFoucsed ? (byte) 1 : (byte) 0);
    }

    protected StatusType(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.priority = in.readInt();
        this.isFoucsed = in.readByte() != 0;
    }

    public static final Creator<StatusType> CREATOR = new Creator<StatusType>() {
        @Override
        public StatusType createFromParcel(Parcel source) {
            return new StatusType(source);
        }

        @Override
        public StatusType[] newArray(int size) {
            return new StatusType[size];
        }
    };
}
