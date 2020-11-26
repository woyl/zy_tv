package com.zhongyou.meettvapplicaion.entities;

/**
 * Created by Administrator on 2016/12/19.
 */

public class GlobalConfigurationInformation {
    private int errcode;
    private String errmsg;
    private GlobalConfigurationInformations data;

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

    public GlobalConfigurationInformations getData() {
        return data;
    }

    public void setData(GlobalConfigurationInformations data) {
        this.data = data;
    }


    public class GlobalConfigurationInformations{
        private GlobalConfigurationBean staticRes;

        public GlobalConfigurationBean getStaticRes() {
            return staticRes;
        }

        public void setStaticRes(GlobalConfigurationBean staticRes) {
            this.staticRes = staticRes;
        }

        public class GlobalConfigurationBean{
            private String imgUrl;
            private String videoUrl;
            private String downloadUrl;
            private String cooperationUrl;

            public String getImgUrl() {
                return imgUrl;
            }

            public void setImgUrl(String imgUrl) {
                this.imgUrl = imgUrl;
            }

            public String getVideoUrl() {
                return videoUrl;
            }

            public void setVideoUrl(String videoUrl) {
                this.videoUrl = videoUrl;
            }

            public String getDownloadUrl() {
                return downloadUrl;
            }

            public void setDownloadUrl(String downloadUrl) {
                this.downloadUrl = downloadUrl;
            }

            public String getCooperationUrl() {
                return cooperationUrl;
            }

            public void setCooperationUrl(String cooperationUrl) {
                this.cooperationUrl = cooperationUrl;
            }


        }
    }
}
