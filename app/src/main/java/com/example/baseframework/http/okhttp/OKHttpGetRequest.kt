package com.example.baseframework.http.okhttp

import com.example.baseframework.http.interfaces.Method
import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnNetCallback

class OKHttpGetRequest(url :String):OKHttpRequestImpl<OnNetCallback<Response>,Response>(url, Method.METHOD_GET) {

    override fun setParams(params: HashMap<String, String>): Request<OnNetCallback<Response>, Response> {
        throw UnsupportedOperationException()
    }

    override fun setParams(mediaType: String, param: String): Request<OnNetCallback<Response>, Response> {
        throw UnsupportedOperationException()
    }

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