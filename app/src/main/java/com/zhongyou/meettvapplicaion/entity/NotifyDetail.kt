package com.zhongyou.meettvapplicaion.entity

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/9 12:53 PM.
 * @
 */
data class NotifyDetail(
    val id: String,
    val isPush: Int,
    val isRecord: Int,
    val isToken: Int,
    val meetingId: String,
    val notice: String,
    val noticeType: Int,
    val resolvingPower: Int,
    val time: String,
    val type: Int
)