package com.zhongyou.meettvapplicaion.entities;

/**
 * @author Dongce
 * create time: 2018/10/20
 */
public class WechatLogin {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "WechatLogin{" +
                "url='" + url + '\'' +
                '}';
    }
}
