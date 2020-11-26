package com.zhongyou.meettvapplicaion.event;

import com.orhanobut.logger.Logger;

/**
 * Created by whatisjava on 17-9-11.
 */

public class ResolutionChangeEvent {

    private int resolution;

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        Logger.e("ResolutionChangeEvent:"+resolution);
        this.resolution = resolution;
    }
}
