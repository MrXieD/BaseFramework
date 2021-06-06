package com.example.baseframework.test

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.os.Message

class TestJavaActitivy : Activity() {
    private val test: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }
}