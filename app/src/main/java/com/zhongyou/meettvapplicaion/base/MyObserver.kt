package com.zhongyou.meettvapplicaion.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.Disposable

/**
 * @author golangdorid@gmail.com
 * @date 2020/6/2 2:25 PM.
 * @
 */

open class MyObserver : LifecycleObserver {

    val disposableLists = ArrayList<Disposable>()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        for (disposableList in disposableLists) {
            if (!disposableList.isDisposed) {
                disposableList.dispose()
            }
        }
    }
}