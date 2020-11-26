package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Created by yuna on 2017/7/27.
 */

public class Materials implements Entity, Parcelable {

    private int pageNo;

    private int totalPage;

    private int pageSize;

    private int totalCount;

    private int count;

    private ArrayList<Material> pageData;

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

    public ArrayList<Material> getPageData() {
        return pageData;
    }

    public void setPageData(ArrayList<Material> pageData) {
        this.pageData = pageData;
    }

    @Override
    public String toString() {
        return "Materials{" +
                "pageNo=" + pageNo +
                ", totalPage=" + totalPage +
                ", pageSize=" + pageSize +
                ", totalCount=" + totalCount +
                ", count=" + count +
                ", pageData=" + pageData +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Materials materials = (Materials) o;
        return pageNo == materials.pageNo &&
                totalPage == materials.totalPage &&
                pageSize == materials.pageSize &&
                totalCount == materials.totalCount &&
                count == materials.count &&
                Objects.equals(pageData, materials.pageData);
    }

    @Override
    public int hashCode() {

        return Objects.hash(pageNo, totalPage, pageSize, totalCount, count, pageData);
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
        dest.writeTypedList(this.pageData);
    }

    public Materials() {
    }

    protected Materials(Parcel in) {
        this.pageNo = in.readInt();
        this.totalPage = in.readInt();
        this.pageSize = in.readInt();
        this.totalCount = in.readInt();
        this.count = in.readInt();
        this.pageData = in.createTypedArrayList(Material.CREATOR);
    }

    public static final Creator<Materials> CREATOR = new Creator<Materials>() {
        @Override
        public Materials createFromParcel(Parcel source) {
            return new Materials(source);
        }

        @Override
        public Materials[] newArray(int size) {
            return new Materials[size];
        }
    };
}
