package com.zhongyou.meettvapplicaion.ui

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.databinding.ObservableArrayList
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.zhongyou.meettvapplicaion.R
import com.zhongyou.meettvapplicaion.BR
import com.zhongyou.meettvapplicaion.base.BaseViewModel
import com.zhongyou.meettvapplicaion.entity.PPT
import com.zhongyou.meettvapplicaion.network.RxSchedulersHelper
import com.zhongyou.meettvapplicaion.network.RxSubscriber
import io.reactivex.disposables.Disposable
import me.tatarka.bindingcollectionadapter2.BindingCollectionAdapter
import me.tatarka.bindingcollectionadapter2.BindingRecyclerViewAdapter
import me.tatarka.bindingcollectionadapter2.ItemBinding


/**
 * @author golangdorid@gmail.com
 * @date 2020/6/1 4:05 PM.
 * @
 */
class DemoViewModel(application: Application) : BaseViewModel(application) {
    val datalists = ObservableArrayList<PPT>()

    val itemBinding = ItemBinding.of<PPT>(BR.item, R.layout.main_item)

//    val adapter = BindingRecyclerViewAdapter<PPT>()
    /* fun getListMutableLiveData(): MutableLiveData<List<PPT>> {
         return datalists
     }*/

    fun getData() {
        mApiService.getAllPPT("41cca7acfe2741c8806762a9833eec16").compose(RxSchedulersHelper.io_main())
                .subscribe(object : RxSubscriber<JSONObject>() {
                    override fun onSubscribe(d: Disposable) {
//                        super.onSubscribe(d)
                    }
                    override fun _onNext(t: JSONObject?) {
                        t?.let {
                            if (t.getInteger("errcode") == 0) {
                                var jsonArray = t.getJSONArray("data")
                                var parseArray = JSON.parseArray(jsonArray.toJSONString(), PPT::class.java)
                                datalists.addAll(parseArray)

                            }
                        }

                    }
                })
    }


}