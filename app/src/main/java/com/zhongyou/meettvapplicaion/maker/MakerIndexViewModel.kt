package com.zhongyou.meettvapplicaion.maker

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import com.alibaba.fastjson.JSON
import com.zhongyou.meettvapplicaion.BaseApplication
import com.zhongyou.meettvapplicaion.base.BaseViewModel
import com.zhongyou.meettvapplicaion.entity.RecomandData
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper
import com.zhongyou.meettvapplicaion.network.RxSubscriber
import io.reactivex.disposables.Disposable

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/4 9:36 AM.
 * @
 */
class MakerIndexViewModel(application: Application) : BaseViewModel(application) {
    val dataLists = MutableLiveData<RecomandData>()


    fun getBannerData(): MutableLiveData<RecomandData> {
        return dataLists
    }

    /*获取轮播图*/
    fun getRecomandData() {
        mApiService.getPromotionPageId(2).compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<com.alibaba.fastjson.JSONObject>() {
                    override fun onSubscribe(d: Disposable) {
//                        super.onSubscribe(d)
                    }

                    override fun _onNext(t: com.alibaba.fastjson.JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                val recomandData: RecomandData = JSON.parseObject<RecomandData>(t.getJSONObject("data").toJSONString(), RecomandData::class.java)
                                dataLists.value = recomandData
                            }
                        }

                    }
                })
    }

    fun notifyButtonClick() {
        getApplication<BaseApplication>().startActivity(Intent(getApplication(), NotifyActivity::class.java))
    }


}