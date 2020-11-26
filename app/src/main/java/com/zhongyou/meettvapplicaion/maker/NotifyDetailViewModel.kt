package com.zhongyou.meettvapplicaion.maker

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableArrayList
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.zhongyou.meettvapplicaion.base.BaseViewModel
import com.zhongyou.meettvapplicaion.entity.NotifyDetail
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper
import com.zhongyou.meettvapplicaion.network.RxSubscriber
import io.reactivex.disposables.Disposable

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/9 11:15 AM.
 * @
 */

class NotifyDetailViewModel(application: Application) : BaseViewModel(application) {
    var title = MutableLiveData<String>()
    var type = MutableLiveData<String>()







}