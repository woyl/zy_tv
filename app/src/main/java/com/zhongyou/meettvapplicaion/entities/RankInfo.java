package com.zhongyou.meettvapplicaion.entities;

/**Retrieve specific user rating info
 * Created by wufan on 2017/8/15.
 */

public class RankInfo {

    /**
     * star : 3.0
     * serviceFrequency : 20
     * ratingFrequency : 18
     */

    private String star;
    private String ratingRate;
    private int serviceFrequency;
    private int ratingFrequency;

    public String getStar() {
        return star;
    }

    public String getRatingRate() {
        return ratingRate;
    }

    public void setRatingRate(String ratingRate) {
        this.ratingRate = ratingRate;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public int getServiceFrequency() {
        return serviceFrequency;
    }

    public void setServiceFrequency(int serviceFrequency) {
        this.serviceFrequency = serviceFrequency;
    }

    public int getRatingFrequency() {
        return ratingFrequency;
    }

    public void setRatingFrequency(int ratingFrequency) {
        this.ratingFrequency = ratingFrequency;
    }

    @Override
    public String toString() {
        return "RankInfo{" +
                "star='" + star + '\'' +
                ", ratingRate='" + ratingRate + '\'' +
                ", serviceFrequency=" + serviceFrequency +
                ", ratingFrequency=" + ratingFrequency +
                '}';
    }
}
