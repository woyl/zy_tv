package com.zhongyou.meettvapplicaion.base

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import com.zhongyou.meettvapplicaion.BaseApplication
import com.zhongyou.meettvapplicaion.network.ApiService
import com.zhongyou.meettvapplicaion.network.HttpsRequest
import io.reactivex.disposables.Disposable
import org.jetbrains.anko.AnkoLogger

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/1 1:11 PM.
 * @
 */
open class BaseViewModel(application: Application) : AndroidViewModel(application), AnkoLogger {


    val disposableLists = ArrayList<Disposable>()

    fun addDisposable(disposable: Disposable) {
        disposableLists.add(disposable)
    }

    protected val mApiService: ApiService by lazy {
        HttpsRequest.provideClientApi()
    }

    public override fun onCleared() {
        super.onCleared()
       /* for (disposable in disposableLists) {
            if (!disposable.isDisposed) {
                disposable.dispose()
            }
        }*/
    }
}