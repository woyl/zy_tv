package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.LinkedList;

/**
 * @author Dongce
 * create time: 2018/11/20
 */
public class ForumContent implements Parcelable, Entity {

    private int pageNo;
    private int totalPage;
    private int pageSize;
    private LinkedList<PageData> pageData;
    private int totalCount;

    protected ForumContent(Parcel in) {
        pageNo = in.readInt();
        totalPage = in.readInt();
        pageSize = in.readInt();
        totalCount = in.readInt();
        in.readTypedList(pageData, PageData.CREATOR);
    }

    public static final Creator<ForumContent> CREATOR = new Creator<ForumContent>() {
        @Override
        public ForumContent createFromParcel(Parcel in) {
            return new ForumContent(in);
        }

        @Override
        public ForumContent[] newArray(int size) {
            return new ForumContent[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(pageNo);
        dest.writeInt(totalPage);
        dest.writeInt(pageSize);
        dest.writeInt(totalCount);
        dest.writeTypedList(pageData);
    }

    @Override
    public String toString() {
        return "ForumContent{" +
                "pageNo='" + pageNo +
                ", totalPage='" + totalPage +
                ", pageSize='" + pageSize +
                ", pageData='" + pageData +
                ", totalCount='" + totalCount +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForumContent forumContent = (ForumContent) o;

        if (pageNo != forumContent.pageNo) return false;
        if (totalPage != forumContent.totalPage) return false;
        if (pageSize != forumContent.pageSize) return false;
        if (pageData != null ? !pageData.equals(forumContent.pageData) : forumContent.pageData == null)
            return false;
        return totalCount != forumContent.totalCount;
    }

    @Override
    public int hashCode() {
        int result = pageNo;
        result = 31 * result + totalPage;
        result = 31 * result + pageSize;
        result = 31 * result + (pageData != null ? pageData.hashCode() : 0);
        result = 31 * result + totalCount;
        return result;
    }

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

    public LinkedList<PageData> getPageData() {
        return pageData;
    }

    public void setPageData(LinkedList<PageData> pageData) {
        this.pageData = pageData;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
