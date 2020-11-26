package com.zhongyou.meettvapplicaion.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by wufan on 2017/8/2.
 */

public class RecordData {

    /**
     * pageNo : 1
     * totalPage : 1
     * pageSize : 20
     * pageData : [{"callAnswerTime":"17:58","minuteInterval":20,"address":"中关村店","callEndTime":"2017-07-27 18:18:58.0","callStartTime":"2017-07-27 17:58:08.0","name":"啦啦啦123","mobile":"138****3620","id":"0d1ac3895f4b41eb8dfdb2da55d02891","status":1}]
     * totalCount : 13
     */

    private int pageNo;
    private int totalPage;
    private int pageSize;
    private int totalCount;
    private List<PageDataEntity> pageData;

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

    public List<PageDataEntity> getPageData() {
        return pageData;
    }

    public void setPageData(List<PageDataEntity> pageData) {
        this.pageData = pageData;
    }

    public static class PageDataEntity implements Parcelable {
        /**
         * address : 平谷店
         * callEndTime : 2017-08-15 13:40:32.0
         * ratingContent :
         * callStartTime : 2017-08-15 11:46:45.0
         * mobile : 151****6834
         * expostorId : c94031c045764b25b3dffb8f4365b24a
         * photo : http://wx.qlogo.cn/mmopen/qzzxIR86jCBzfq8D20spFE1tgUUj9J3eJylYZOOo1T1xVliaThPMqUnWPjYYpWskQiaMlXw5VBaOp1DJSbalHIqibrm9dL464We/0
         * clerkId : cd7456c9880e4a60a874c5f387d40010
         * secondInterval : 45
         * replyRating :
         * ratingStar : 5
         * callAnswerTime : 2017-08-15 11:46:47.0
         * minuteInterval : 113
         * name : 艾浩轩
         * id : a2b5c6f0042d4b8fa28881e16fdb4e93
         * status : 9
         */

        private String address;
        private String callEndTime;
        private String ratingContent;
        private String callStartTime;
        private String mobile;
        private String expostorId;
        private String photo;
        private String clerkId;
        private int secondInterval;
        private String replyRating;
        private int ratingStar;
        private String callAnswerTime;
        private int minuteInterval;
        private String name;
        private String id;
        private int status;

        //status (integer, optional): 状态， 0：未应答（30s未接听），1：接听，2：拒绝, 8：对方挂断 9：通话结束挂断 ,



        public  String getOrderTime(){
            if(!TextUtils.isEmpty(callAnswerTime)){
                return callAnswerTime;
            }else{
                return callStartTime;
            }
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCallEndTime() {
            return callEndTime;
        }

        public void setCallEndTime(String callEndTime) {
            this.callEndTime = callEndTime;
        }

        public String getRatingContent() {
            return ratingContent;
        }

        public void setRatingContent(String ratingContent) {
            this.ratingContent = ratingContent;
        }

        public String getCallStartTime() {
            return callStartTime;
        }

        public void setCallStartTime(String callStartTime) {
            this.callStartTime = callStartTime;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getExpostorId() {
            return expostorId;
        }

        public void setExpostorId(String expostorId) {
            this.expostorId = expostorId;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }

        public String getClerkId() {
            return clerkId;
        }

        public void setClerkId(String clerkId) {
            this.clerkId = clerkId;
        }

        public int getSecondInterval() {
            return secondInterval;
        }

        public void setSecondInterval(int secondInterval) {
            this.secondInterval = secondInterval;
        }

        public String getReplyRating() {
            return replyRating;
        }

        public void setReplyRating(String replyRating) {
            this.replyRating = replyRating;
        }

        public int getRatingStar() {
            return ratingStar;
        }

        public void setRatingStar(int ratingStar) {
            this.ratingStar = ratingStar;
        }

        public String getCallAnswerTime() {
            return callAnswerTime;
        }

        public void setCallAnswerTime(String callAnswerTime) {
            this.callAnswerTime = callAnswerTime;
        }

        public int getMinuteInterval() {
            return minuteInterval;
        }

        public void setMinuteInterval(int minuteInterval) {
            this.minuteInterval = minuteInterval;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.address);
            dest.writeString(this.callEndTime);
            dest.writeString(this.ratingContent);
            dest.writeString(this.callStartTime);
            dest.writeString(this.mobile);
            dest.writeString(this.expostorId);
            dest.writeString(this.photo);
            dest.writeString(this.clerkId);
            dest.writeInt(this.secondInterval);
            dest.writeString(this.replyRating);
            dest.writeInt(this.ratingStar);
            dest.writeString(this.callAnswerTime);
            dest.writeInt(this.minuteInterval);
            dest.writeString(this.name);
            dest.writeString(this.id);
            dest.writeInt(this.status);
        }

        public PageDataEntity() {
        }

        protected PageDataEntity(Parcel in) {
            this.address = in.readString();
            this.callEndTime = in.readString();
            this.ratingContent = in.readString();
            this.callStartTime = in.readString();
            this.mobile = in.readString();
            this.expostorId = in.readString();
            this.photo = in.readString();
            this.clerkId = in.readString();
            this.secondInterval = in.readInt();
            this.replyRating = in.readString();
            this.ratingStar = in.readInt();
            this.callAnswerTime = in.readString();
            this.minuteInterval = in.readInt();
            this.name = in.readString();
            this.id = in.readString();
            this.status = in.readInt();
        }

        public static final Creator<PageDataEntity> CREATOR = new Creator<PageDataEntity>() {
            @Override
            public PageDataEntity createFromParcel(Parcel source) {
                return new PageDataEntity(source);
            }

            @Override
            public PageDataEntity[] newArray(int size) {
                return new PageDataEntity[size];
            }
        };
    }
}
