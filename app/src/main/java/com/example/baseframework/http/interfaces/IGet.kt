package com.example.baseframework.http.interfaces

import com.example.baseframework.http.interfaces.callback.OnNetCallback
import com.example.baseframework.http.Request
import com.example.baseframework.http.Response

interface IGet {
    fun get(url:String,listener: OnNetCallback<Response>?=null): Request<OnNetCallback<Response>, Response>
}