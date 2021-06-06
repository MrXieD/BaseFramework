package com.anthonyh.afuweather.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

/**
@author Anthony.H
@date: 2021/1/15 0015
@desription:
 */
open abstract class BasePresenter<VIEW : BaseView> : LifecycleObserver {


    protected var view: VIEW? = null

    @Suppress("UNCHECKED_CAST")
    fun attachView(v: Any?) {
        if(v is BaseView){
            view = v as VIEW
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        this.view = null
    }


}