package com.example.baseframework.http

import com.example.baseframework.http.interfaces.callback.OnNetCallback

interface Request<Callback : OnNetCallback<Result>, Result> {
    /**
     * 设置请求头部
     */
    fun setHeader(headers: HashMap<String, String>): Request<Callback, Result>

    /**
     * 设置Params
     */
    fun setParams(params: HashMap<String, String>): Request<Callback, Result>

    /**
     * 设置状态回调监听器。
     */
    fun setListener(listener: Callback?): Request<Callback, Result>

    fun onSuccess(callback: (Result) -> Unit): Request<Callback, Result>

    fun onFailure(callback: (Response) -> Unit): Request<Callback, Result>
    /**
     * 进度监听
     */
    fun onProgress(callback: (curr:Long,total:Long) -> Unit): Request<Callback, Result>
    /**
     * 设置连接超时时间，毫秒
     */
    fun setConnectTimeout(time: Long): Request<Callback, Result>

    /**
     * 设置读取超时时间，毫秒
     */
    fun setReadTimeout(time: Long): Request<Callback, Result>

    /**
     * 设置写入超时时间，毫秒
     */
    fun setWriteTimeout(time: Long): Request<Callback, Result>

    /**
     * 异步执行
     */
    fun connect(): Request<Callback, Result>
    /**
     * 取消该Request
     */
    fun cancel()
}