package com.zhongyou.meettvapplicaion.entity

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/4 11:05 AM.
 * @
 */
data class AudioItem(
        val anchorName: String?,
        val belongTime: String?,
        val comentURL: String?,
        val name: String?,
        val onLineTime: String?,
        val totalDate: String?,
        val vioceURL: String?,
        var currentType: Int=0,//0 默认状态 1播放状态   2 暂停状态 3 加载中,
        var currentPlayTime: Int=0
)