package com.zhongyou.meettvapplicaion.maker

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.view.View
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.zhongyou.meettvapplicaion.BaseApplication
import com.zhongyou.meettvapplicaion.R
import com.zhongyou.meettvapplicaion.base.BaseViewModel
import com.zhongyou.meettvapplicaion.entity.Notify
import com.zhongyou.meettvapplicaion.entity.RecomandData
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper
import com.zhongyou.meettvapplicaion.network.RxSubscriber
import io.reactivex.disposables.Disposable

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/9 9:24 AM.
 * @
 */

class NotifyViewModel(application: Application) : BaseViewModel(application) {
    val dataLists = MutableLiveData<List<Notify>>()

    var classNotifyCount = MutableLiveData<String>()
    var makerNotifyCount = MutableLiveData<String>()
    var systemNotifyCount = MutableLiveData<String>()

    init {
        classNotifyCount.value = "0"
        makerNotifyCount.value = "0"
        systemNotifyCount.value = "0"
    }

    fun getNotifyList(): MutableLiveData<List<Notify>> {
        return dataLists
    }



    fun getNotifyData() {
        mApiService.noticeMessageTypeByUserId.compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {
//                        super.onSubscribe(d)
                    }

                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                val toJSONString = t.getJSONArray("data").toJSONString()
                                val parseArray = JSON.parseArray(toJSONString, Notify::class.java)
                                dataLists.value = parseArray
                            }
                        }
                    }

                })
    }

}