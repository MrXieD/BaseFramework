package com.example.baseframework.http.okhttp

import com.example.baseframework.http.interfaces.Method
import com.example.baseframework.http.Request
import com.example.baseframework.http.interfaces.callback.OnFileDownloadListener
import okhttp3.Response
import java.io.File
import java.io.FileOutputStream

class FileDownloadRequest(url: String) : OKHttpRequestImpl<OnFileDownloadListener, File>(url, Method.METHOD_DOWNLOAD) {
    private var mDownloadFile: File? = null

    fun setDownloadFile(downloadFile: File): Request<OnFileDownloadListener, File> {
        mDownloadFile = downloadFile
        return this
    }
    override fun handlerResponse(response: Response): OKHttpResponse {
        return readResponse(response).apply {
            if (isSuccessful()) {
                onSuccessCallback(mDownloadFile!!)
            } else {
                onFailureCallback(this)
            }
        }
    }

    private fun readResponse(response: Response): OKHttpResponse {
        if (!response.isSuccessful) {
            return OKHttpResponse(response)
        }
        if (mDownloadFile == null) {
            return OKHttpResponse(NullPointerException())
        }
        var result = OKHttpResponse(response)
        val downloadFile = mDownloadFile!!
        val totalLen = response.body?.contentLength() ?: 0L
        downloadFile.parentFile?.mkdirs()
        var out: FileOutputStream? = null
        try {
            result.getBodyOfByteStream {
                it?.apply {
                    out = FileOutputStream(downloadFile)
                    var current = 0L
                    val buff = ByteArray(DEFAULT_BUFFER_SIZE)
                    var readNum: Int
                    while (true) {
                        readNum = read(buff)
                        if (readNum == -1) {
                            break
                        }
                        current += readNum
                        out!!.write(buff, 0, readNum)
                        onProgressCallback(current, totalLen)
                    }
                }
            }
            if (totalLen >= 0 && downloadFile.length() != totalLen) {
                result = OKHttpResponse(IllegalArgumentException("File Error"), response)
            }
        } catch (e: Exception) {
            downloadFile.delete()
            return OKHttpResponse(e, response)
        } finally {
            out?.apply {
                try {
                    flush()
                    fd.sync()
                    close()
                } catch (e: Exception) {

                }
            }
        }
        return result
    }
}