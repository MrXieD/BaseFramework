package com.example.baseframework.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

open class BaseVBActivity<VB : ViewBinding> : AppCompatActivity() {
   protected lateinit var mViewContainer:VB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewContainer = ViewBindingUtil.create(this::class.java,layoutInflater)
        setContentView(mViewContainer.root)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}