package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class ConvertFileList implements Parcelable {
    private String name;
    private Ppt ppt;

    protected ConvertFileList(Parcel in) {
        name = in.readString();
    }

    public static final Creator<ConvertFileList> CREATOR = new Creator<ConvertFileList>() {
        @Override
        public ConvertFileList createFromParcel(Parcel in) {
            return new ConvertFileList(in);
        }

        @Override
        public ConvertFileList[] newArray(int size) {
            return new ConvertFileList[size];
        }
    };

    public Ppt getPpt() {
        return ppt;
    }

    public void setPpt(Ppt ppt) {
        this.ppt = ppt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ConvertFileList{" +
                "name='" + name + '\'' +
                ", ppt=" + ppt +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
    }

    public class Ppt implements Parcelable{
        String src;
        String width;
        String height;

        protected Ppt(Parcel in) {
            src = in.readString();
            width = in.readString();
            height = in.readString();
        }

        public final Creator<Ppt> CREATOR = new Creator<Ppt>() {
            @Override
            public Ppt createFromParcel(Parcel in) {
                return new Ppt(in);
            }

            @Override
            public Ppt[] newArray(int size) {
                return new Ppt[size];
            }
        };

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getWidth() {
            return width;
        }

        public void setWidth(String width) {
            this.width = width;
        }

        public String getHeight() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        @Override
        public String toString() {
            return "Ppt{" +
                    "src='" + src + '\'' +
                    ", width='" + width + '\'' +
                    ", height='" + height + '\'' +
                    '}';
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(src);
            dest.writeString(width);
            dest.writeString(height);
        }
    }
}
