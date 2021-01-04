package com.example.baseframework.http.interfaces

import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnFileDownloadListener
import com.example.baseframework.http.interfaces.callback.OnFileUploadListener
import java.io.File

interface IFile {
    fun downloadFile(url: String, downloadFile: File, listener: OnFileDownloadListener? = null): Request<OnFileDownloadListener, File>
    fun uploadFile(url: String, fileKey: String, file: File, listener: OnFileUploadListener? = null): Request<OnFileUploadListener, Response>
    fun uploadFiles(url: String, uploadFiles: HashMap<String, File>, listener: OnFileUploadListener? = null): Request<OnFileUploadListener, Response>
}