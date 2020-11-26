package com.zhongyou.meettvapplicaion.maker

import android.app.Application
import android.databinding.ObservableField
import com.zhongyou.meettvapplicaion.base.BaseViewModel

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/5 1:23 PM.
 * @
 */
class MakerDetailModel(application: Application) : BaseViewModel(application) {
    var title = ObservableField<String>()
}