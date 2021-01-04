package com.xiedi.testapp.test.broken.animator

import android.view.View

interface BrokenCallback {
    fun onStart(v: View){}

    fun onCancel(v: View){}

    fun onRestart(v: View){}

    fun onFalling(v: View){}

    fun onFallingEnd(v: View){}

    fun onCancelEnd(v: View){}
}