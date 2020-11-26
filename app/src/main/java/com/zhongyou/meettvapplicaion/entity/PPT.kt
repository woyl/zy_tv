package com.zhongyou.meettvapplicaion.entity

data class PPT(
    val auditStatus: Int,
    val createDate: String,
    val delFlag: String,
    val id: String,
    val meetingId: String,
    val meetingMaterialsPublishList: List<MeetingMaterialsPublish>,
    val meetingName: String,
    val name: String,
    val type: Int,
    val updateDate: String,
    val userId: String
)