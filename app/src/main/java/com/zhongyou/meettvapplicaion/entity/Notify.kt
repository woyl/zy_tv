package com.zhongyou.meettvapplicaion.entity

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/9 10:32 AM.
 * @
 */
data class Notify(
        val notice: String,
        val number: Int,
        val time: String,
        val type: Int
)