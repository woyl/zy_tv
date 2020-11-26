package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Objects;

public class StorageBean implements Parcelable {
    private String path;
    private String mounted;
    private boolean removable;
    private long totalSize;
    private long availableSize;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMounted() {
        return mounted;
    }

    public void setMounted(String mounted) {
        this.mounted = mounted;
    }

    public boolean getRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getAvailableSize() {
        return availableSize;
    }

    public void setAvailableSize(long availableSize) {
        this.availableSize = availableSize;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.path);
        dest.writeString(this.mounted);
        dest.writeByte(removable ? (byte) 1 : (byte) 0);
        dest.writeLong(this.totalSize);
        dest.writeLong(this.availableSize);
    }

    public StorageBean() {
    }

    protected StorageBean(Parcel in) {
        this.path = in.readString();
        this.mounted = in.readString();
        this.removable = in.readByte() != 0;
        this.totalSize = in.readLong();
        this.availableSize = in.readLong();
    }

    public static final Creator<StorageBean> CREATOR = new Creator<StorageBean>() {
        @Override
        public StorageBean createFromParcel(Parcel source) {
            return new StorageBean(source);
        }

        @Override
        public StorageBean[] newArray(int size) {
            return new StorageBean[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StorageBean that = (StorageBean) o;
        return removable == that.removable &&
                totalSize == that.totalSize &&
                availableSize == that.availableSize &&
                Objects.equals(path, that.path) &&
                Objects.equals(mounted, that.mounted);
    }

    @Override
    public int hashCode() {

        return Objects.hash(path, mounted, removable, totalSize, availableSize);
    }

    @Override
    public String toString() {
        return "StorageBean{" +
                "path='" + path + '\'' +
                ", mounted='" + mounted + '\'' +
                ", removable=" + removable +
                ", totalSize=" + totalSize +
                ", availableSize=" + availableSize +
                '}';
    }
}
