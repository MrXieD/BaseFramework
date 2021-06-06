package com.example.baseframework.activity

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.example.baseframework.ex.statusBarHeight

/**
@author Anthony.H
@date: 2021/5/18
@desription:
 */
abstract class BaseDBActivity<VDB : ViewDataBinding> : AppCompatActivity() {

    protected lateinit var dataBinding: VDB


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding = DataBindingUtil.setContentView<VDB>(
            this,
            getLayoutId()
        )
        val layoutParams = dataBinding.root.layoutParams
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.topMargin += applicationContext.statusBarHeight
            dataBinding.root.layoutParams = layoutParams
        }
        init()
    }

    abstract fun init()

    abstract fun getLayoutId(): Int


}