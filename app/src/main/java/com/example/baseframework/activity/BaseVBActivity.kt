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
        mViewContainer.root.fitsSystemWindows = isFitsSystemWindows()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * ContentView是否延伸到状态栏或底部导航栏
     * true = 不延展到状态栏或底部导航栏
     * false = 延展到状态栏或底部导航栏
     */
    protected open fun isFitsSystemWindows() = true
}