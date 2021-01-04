package com.example.baseframework.http.interfaces.callback

import java.io.File

interface OnFileDownloadListener:OnNetCallback<File> {
    fun onProgress(curr:Long,total:Long)
}