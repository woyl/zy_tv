package com.zhongyou.meettvapplicaion.event;

import com.zhongyou.meettvapplicaion.entities.RankInfo;

/**
 * Created by whatisjava on 17-8-30.
 */

public class UpdateRatingEvent {

    private RankInfo rankInfo;

    public RankInfo getRankInfo() {
        return rankInfo;
    }

    public void setRankInfo(RankInfo rankInfo) {
        this.rankInfo = rankInfo;
    }

    @Override
    public String toString() {
        return "UpdateRatingEvent{" +
                "rankInfo=" + rankInfo +
                '}';
    }
}
