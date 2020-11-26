package com.zhongyou.meettvapplicaion.entities;

import java.util.Objects;

public class QRCodeUrl implements Entity {

    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "QRCodeUrl{" +
                "url='" + url + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        QRCodeUrl qrCodeUrl = (QRCodeUrl) o;
        return Objects.equals(url, qrCodeUrl.url);
    }

    @Override
    public int hashCode() {

        return Objects.hash(url);
    }
}
