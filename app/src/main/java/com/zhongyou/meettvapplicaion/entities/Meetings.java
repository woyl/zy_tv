package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by yuna on 2017/7/27.
 */

public class Meetings implements Entity, Parcelable {

    private int pageNo;

    private int totalPage;

    private int pageSize;

    private int totalCount;

    private int count;

    private ArrayList<Meeting> list;

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public ArrayList<Meeting> getList() {
        return list;
    }

    public void setList(ArrayList<Meeting> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "Meetings{" +
                "pageNo=" + pageNo +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", count=" + count +
                ", list=" + list +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Meetings meetings = (Meetings) o;

        if (pageNo != meetings.pageNo) return false;
        if (totalPage != meetings.totalPage) return false;
        if (pageSize != meetings.pageSize) return false;
        if (totalCount != meetings.totalCount) return false;
        if (count != meetings.count) return false;
        return list != null ? list.equals(meetings.list) : meetings.list == null;
    }

    @Override
    public int hashCode() {
        int result = pageNo;
        result = 31 * result + totalPage;
        result = 31 * result + pageSize;
        result = 31 * result + totalCount;
        result = 31 * result + count;
        result = 31 * result + (list != null ? list.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.pageNo);
        dest.writeInt(this.totalPage);
        dest.writeInt(this.pageSize);
        dest.writeInt(this.totalCount);
        dest.writeInt(this.count);
        dest.writeTypedList(this.list);
    }

    public Meetings() {
    }

    protected Meetings(Parcel in) {
        this.pageNo = in.readInt();
        this.totalPage = in.readInt();
        this.pageSize = in.readInt();
        this.totalCount = in.readInt();
        this.count = in.readInt();
        this.list = in.createTypedArrayList(Meeting.CREATOR);
    }

    public static final Creator<Meetings> CREATOR = new Creator<Meetings>() {
        @Override
        public Meetings createFromParcel(Parcel source) {
            return new Meetings(source);
        }

        @Override
        public Meetings[] newArray(int size) {
            return new Meetings[size];
        }
    };
}
