package com.example.baseframework.http.okhttp

import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.IFile
import com.example.baseframework.http.interfaces.IGet
import com.example.baseframework.http.interfaces.IPost
import com.example.baseframework.http.interfaces.callback.OnFileDownloadListener
import com.example.baseframework.http.interfaces.callback.OnFileUploadListener
import com.example.baseframework.http.interfaces.callback.OnNetCallback
import java.io.File

class OKHttpImpl:IFile, IGet,IPost{
    companion object {
        private const val MEDIA_TYPE_JSON = "application/json; charset=utf-8"
        private const val MEDIA_TYPE_TEXT = "text/plain; charset=utf-8"
    }
    override fun downloadFile(url: String, downloadFile: File, listener: OnFileDownloadListener?): Request<OnFileDownloadListener, File> {
        return  FileDownloadRequest(url).setDownloadFile(downloadFile).setListener(listener)
    }

    override fun uploadFile(url: String, fileKey: String, file: File, listener: OnFileUploadListener?): Request<OnFileUploadListener, Response> {
        return uploadFiles(url, hashMapOf(fileKey to file),listener)
    }

    override fun uploadFiles(url: String, uploadFiles: HashMap<String, File>, listener: OnFileUploadListener?): Request<OnFileUploadListener, Response> {
        return OKHttpFileUploadRequest(url).setUploadFile(uploadFiles).setListener(listener)
    }

    override fun get(url: String, listener: OnNetCallback<Response>?): Request<OnNetCallback<Response>, Response> {
        return OKHttpGetRequest(url).setListener(listener)
    }

    override fun post(url: String, params: HashMap<String, String>, listener: OnNetCallback<Response>?): Request<OnNetCallback<Response>, Response> {
        return OKHttpPostRequest(url).setParams(params).setListener(listener)
    }

    override fun post(url: String, mediaType: String, param: String, listener: OnNetCallback<Response>?): Request<OnNetCallback<Response>, Response> {
        return OKHttpPostRequest(url).setParams(mediaType,param).setListener(listener)
    }

    override fun postJson(url: String, json: String, listener: OnNetCallback<Response>?): Request<OnNetCallback<Response>, Response> {
        return post(url,MEDIA_TYPE_JSON,json,listener)
    }

    override fun postText(url: String, text: String, listener: OnNetCallback<Response>?): Request<OnNetCallback<Response>, Response> {
        return post(url,MEDIA_TYPE_TEXT,text,listener)
    }

}