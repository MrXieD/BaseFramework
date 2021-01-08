package com.example.baseframework.http.okhttp

import com.example.baseframework.http.interfaces.Method
import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnNetCallback

class OKHttpPostRequest(url:String) : OKHttpRequestImpl<OnNetCallback<Response>,Response>(url, Method.METHOD_POST) {

    override fun onProgress(progress: (current: Long, total: Long) -> Unit): Request<OnNetCallback<Response>, Response> {
        throw UnsupportedOperationException()
    }

    override fun handlerResponse(response: okhttp3.Response): Response {
        return OKHttpResponse(response).apply {
            if(isSuccessful()){
                onSuccessCallback(this)
            }else{
                onFailureCallback(this)
            }
        }
    }
}