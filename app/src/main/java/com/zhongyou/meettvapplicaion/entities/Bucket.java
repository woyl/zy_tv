package com.zhongyou.meettvapplicaion.entities;

/**
 * Created by whatisjava on 17-7-27.
 */

public class Bucket<Entity> {

    private int errcode;

    private String errmsg;

    private Entity data;

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

    public Entity getData() {
        return data;
    }

    public void setData(Entity data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Bucket{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", data=" + data +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bucket<?> bucket = (Bucket<?>) o;

        if (errcode != bucket.errcode) return false;
        if (errmsg != null ? !errmsg.equals(bucket.errmsg) : bucket.errmsg != null) return false;
        return data != null ? data.equals(bucket.data) : bucket.data == null;

    }

    @Override
    public int hashCode() {
        int result = errcode;
        result = 31 * result + (errmsg != null ? errmsg.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

}
