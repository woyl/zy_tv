package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by yuna on 2017/7/27.
 */

public class Statuses implements Entity, Parcelable {

    private int pageNo;

    private int totalPage;

    private int pageSize;

    private int totalCount;

    private ArrayList<Status> pageData;

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

    public ArrayList<Status> getPageData() {
        return pageData;
    }

    public void setPageData(ArrayList<Status> pageData) {
        this.pageData = pageData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Statuses statuses = (Statuses) o;
        return pageNo == statuses.pageNo &&
                totalPage == statuses.totalPage &&
                pageSize == statuses.pageSize &&
                totalCount == statuses.totalCount &&
                Objects.equals(pageData, statuses.pageData);
    }

    @Override
    public int hashCode() {

        return Objects.hash(pageNo, totalPage, pageSize, totalCount, pageData);
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
        dest.writeTypedList(this.pageData);
    }

    public Statuses() {
    }

    protected Statuses(Parcel in) {
        this.pageNo = in.readInt();
        this.totalPage = in.readInt();
        this.pageSize = in.readInt();
        this.totalCount = in.readInt();
        this.pageData = in.createTypedArrayList(Status.CREATOR);
    }

    public static final Creator<Statuses> CREATOR = new Creator<Statuses>() {
        @Override
        public Statuses createFromParcel(Parcel source) {
            return new Statuses(source);
        }

        @Override
        public Statuses[] newArray(int size) {
            return new Statuses[size];
        }
    };

    @Override
    public String toString() {
        return "Statuses{" +
                "pageNo=" + pageNo +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", pageData=" + pageData +
                '}';
    }
}
