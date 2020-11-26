package com.zhongyou.meettvapplicaion.utils;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Created by ruoyun on 16/9/24.
 * 事件总线
 */

public class RxBus {

    private static volatile RxBus mInstance;

    private RxBus() {
    }

    public static RxBus getDefault() {
        if (mInstance == null) {
            synchronized (RxBus.class) {
                if (mInstance == null) {
                    mInstance = new RxBus();
                }
            }
        }
        return mInstance;
    }

    private final Subject<Object, Object> _bus = new SerializedSubject<Object, Object>(PublishSubject.create());

    public void send(Object o) {
        _bus.onNext(o);
    }

    public Observable<Object> toObservable() {
        return _bus;
    }


    public static void sendMessage(Object o) {
        RxBus.getDefault().send(o);
    }

    public static Subscription handleMessage(Action1 function) {
        return RxBus.getDefault().toObservable().subscribe(function);
    }


    //---例子---begin-
    private void demo() {
        RxBus.getDefault().send(new TapEvent());
        Subscription subscribe = RxBus.getDefault().toObservable().subscribe(new Action1<Object>() {
            @Override
            public void call(Object event) {
                if (event instanceof TapEvent) {
                    //do something
                } else {
                    //do otherthing
                }
            }
        });
        subscribe.unsubscribe();
    }

    private class TapEvent {}
    //---例子---end-
}
