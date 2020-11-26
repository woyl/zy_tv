package com.zhongyou.meettvapplicaion.entities;

import java.util.ArrayList;

/**
 * 分页数据实体
 *
 * @author Dongce
 * create time: 2018/12/7
 */
public class PaginationData<Entity> {

    /**
     * 当前页数
     */
    private int pageNo;
    /**
     * 总共页数
     */
    private int totalPage;
    /**
     * 当前页内容实体数量
     */
    private int pageSize;
    /**
     * 分页内容实体
     */
    private ArrayList<Entity> pageData;
    /**
     * 数据总共数
     */
    private int totalCount;

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

    public ArrayList<Entity> getPageData() {
        return pageData;
    }

    public void setPageData(ArrayList<Entity> pageData) {
        this.pageData = pageData;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    @Override
    public String toString() {
        return "PaginationData{" +
                "pageNo=" + pageNo +
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

        PaginationData<?> bucket = (PaginationData<?>) o;
        if (pageNo != bucket.pageNo) return false;
        if (totalPage != bucket.totalPage) return false;
        if (pageSize != bucket.pageSize) return false;
        if (pageData != bucket.pageData) return false;
        return totalCount != bucket.totalCount;
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
}
