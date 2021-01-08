package com.example.baseframework.http.okhttp

import com.example.baseframework.http.interfaces.Method
import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnFileUploadListener
import java.io.File

class OKHttpFileUploadRequest(url:String):OKHttpRequestImpl<OnFileUploadListener,Response>(url, Method.METHOD_UPLOAD) {

    override fun setParams(mediaType: String, param: String): Request<OnFileUploadListener, Response> {
        throw UnsupportedOperationException()
    }

    override fun onProgress(progress: (current: Long, total: Long) -> Unit): Request<OnFileUploadListener, Response> {
        throw UnsupportedOperationException()
    }

    override fun setUploadFile(uploadFile: HashMap<String, File>): Request<OnFileUploadListener, Response> {
        require(uploadFile.isNotEmpty()) { "upload file cannot empty" }
        return super.setUploadFile(uploadFile)
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