package com.example.baseframework.http.okhttp

import com.example.baseframework.http.NetException
import com.example.baseframework.http.Response
import okhttp3.ResponseBody
import okhttp3.internal.closeQuietly
import java.io.InputStream
import java.io.Reader

class OKHttpResponse : Response {
    private var mResponse: okhttp3.Response? = null
    private var mException: NetException? = null
    private var isCanceled = false
    private var bodyIsRead = false

    constructor(response: okhttp3.Response) {
        mResponse = response
    }

    constructor(isCancel: Boolean) {
        isCanceled = isCancel
    }

    constructor(exception: Exception) {
        mException = if (exception is NetException) exception else NetException(exception)
    }
    constructor(e: Exception, result: okhttp3.Response) {
        mException = if (e is NetException) e else NetException(e)
        mResponse = result
    }

    constructor(exception: Throwable) {
        mException = if (exception is NetException) exception else NetException(exception)
    }

    override fun isSuccessful(): Boolean {
        return !isCanceled && mException == null && mResponse?.isSuccessful == true
    }

    override fun isCanceled(): Boolean {
        return isCanceled
    }

    override fun getException(): Exception? = mException

    override fun getHeaders(): HashMap<String, ArrayList<String>>? {
        val headers = mResponse?.headers ?: return null
        try {
            val size = headers.size
            val headerMap = HashMap<String, ArrayList<String>>()
            for (i in 0 until size) {
                var list = headerMap[headers.name(i)]
                if (list == null) {
                    list = arrayListOf()
                    headerMap[headers.name(i)] = list
                }
                list.add(headers.value(i))
            }
            return headerMap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getBodyLength(): Long {
        return try {
            mResponse?.body?.contentLength() ?: 0L
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }

    private fun <T> getBody(action: (ResponseBody) -> T?): T? {
        check(!bodyIsRead) { "body content has been read" }
        bodyIsRead = true
        return mResponse?.body?.let {
            try {
                action(it)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            } finally {
                it.close()
            }
        }
    }

    override fun getBody(defBody: String): String {
        return getBodyString() ?: defBody
    }

    override fun getBodyString(): String? {
        return getBody { it.string() }
    }

    override fun getBodyBytes(): ByteArray? {
        return getBody { it.bytes() }
    }

    override fun getBodyOfByteStream(action: (InputStream?) -> Unit) {
        getBody { it.byteStream().use { stream -> action(stream) } }
    }

    override fun getBodyOfCharStream(action: (Reader?) -> Unit) {
        getBody { it.charStream().use { stream -> action(stream) } }
    }

    override fun getResponseCode(): Int = mResponse?.code ?: -1

    override fun getMessage(): String = mResponse?.message ?: ""

    override fun getProtocol(): String = mResponse?.protocol?.toString() ?: ""

    override fun getUrl(): String = mResponse?.request?.url?.toString() ?: ""

    override fun close() {
        mResponse?.closeQuietly()
        mResponse = null
    }

    override fun toString() = "isCanceled=$isCanceled, mResponse=${mResponse}, exception=$mException"
}