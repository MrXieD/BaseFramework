package com.anthonyh.afuweather.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
@author Anthony.H
@date: 2021/1/15 0015
@desription:
 */
open abstract class BaseActivity<Presenter : BasePresenter<*>> : AppCompatActivity(),BaseView {

    protected var presenter: Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter?.attachView(this )
        lifecycle.addObserver(createPresenter())
    }


    abstract fun createPresenter(): Presenter
    override fun onDisLoading() {
    }

    override fun onLoading() {
    }
}