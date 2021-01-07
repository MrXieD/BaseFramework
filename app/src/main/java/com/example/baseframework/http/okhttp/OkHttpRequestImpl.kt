package com.example.baseframework.http.okhttp

import android.annotation.SuppressLint
import com.example.baseframework.http.Method
import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnNetCallback
import com.example.baseframework.utils.TimeUtils
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

abstract class OkHttpRequestImpl<Callback : OnNetCallback<Result>, Result>(protected val url: String, protected val method: Method) : Request<Callback, Result> {
    companion object {
        private const val mDefaultTimeout = 20 * TimeUtils.SECOND
        private val mClient: OkHttpClient

        init {
            val builder = OkHttpClient.Builder()
            builder.connectTimeout(mDefaultTimeout, TimeUnit.MILLISECONDS)
            builder.readTimeout(mDefaultTimeout, TimeUnit.MILLISECONDS)
            builder.writeTimeout(mDefaultTimeout, TimeUnit.MILLISECONDS)
            mClient = builder.build()
        }
    }

    private var mHeaders: HashMap<String, String>? = null
    private var mParams: HashMap<String, String>? = null
    private var mUploadFile: HashMap<String, File>? = null
    private var mRequestBody: RequestBody? = null
    private var mCallback: Callback? = null
    private var mOnSuccess: ((Result) -> Unit)? = null
    private var mOnFailure: ((Response) -> Unit)? = null
    private var mCall: Call? = null

    @Volatile
    private var isCanceled = false
    override fun setHeader(headers: HashMap<String, String>): Request<Callback, Result> {
        mHeaders = headers
        return this
    }

    override fun setParams(params: HashMap<String, String>): Request<Callback, Result> {
        mParams = params
        return this
    }

    override fun setParams(mediaType: String, param: String): Request<Callback, Result> {
        mRequestBody = param.toRequestBody(mediaType.toMediaTypeOrNull())
        return this
    }

    open fun setUploadFile(uploadFile: HashMap<String, File>): Request<Callback, Result> {
        mUploadFile = uploadFile
        return this
    }

    override fun setListener(callback: Callback?): Request<Callback, Result> {
        mCallback = callback
        return this
    }

    override fun onSuccess(callback: (Result) -> Unit): Request<Callback, Result> {
        mOnSuccess = callback
        return this
    }

    override fun onFailure(callback: (Response) -> Unit): Request<Callback, Result> {
        mOnFailure = callback
        return this
    }

    @Synchronized
    protected fun newCall(): Call {
        check(mCall == null) { "The call has already been executed." }
        return mClient.newCall(builderRequest().build()).apply { mCall = this }
    }

    protected fun onSuccessCallback(result: Result) {
        clearCall()
        mOnSuccess?.invoke(result)
        mCallback?.onSuccess(result)
    }

    protected fun onFailureCallback(error: Response) {
        clearCall();
        val errorResponse = if (!error.isCanceled() && isCanceled) OkHttpResponse(true) else error
        mCallback?.onFailure(errorResponse)
        mOnFailure?.invoke(errorResponse)
    }

    private fun clearCall(): Call? {
        val call = mCall
        mCall = null
        return call
    }

    final override fun connect(): Request<Callback, Result> {
        isCanceled = false
        try {
            newCall().enqueue(object : okhttp3.Callback {
                override fun onFailure(call: Call, e: IOException) {
                    onFailureCallback(OkHttpResponse(e))
                }

                override fun onResponse(call: Call, response: okhttp3.Response) {
                    handlerResponse(response)
                }

            })
        } catch (e: Throwable) {
            onFailureCallback(OkHttpResponse(e))
        }
        return this
    }

    protected abstract fun handlerResponse(response: okhttp3.Response): Response

    @SuppressLint("NewApi")
    protected fun builderRequest(): okhttp3.Request.Builder {
        val builder = okhttp3.Request.Builder().url(url)
        mHeaders?.forEach { key, value -> builder.addHeader(key, value) }
        when (method) {
            Method.METHOD_GET -> {
                return builder.get()
            }
            Method.METHOD_POST -> {
                val params = mParams
                if (params != null && params.isNotEmpty()) {
                    val formBuilder = FormBody.Builder().apply {
                        params.forEach { (key, value) -> add(key, value) }
                    }
                    return builder.post(formBuilder.build())
                }
                val requestBody = mRequestBody
                if (requestBody != null) {
                    return builder.post(requestBody)
                }
                return builder.post(okhttp3.internal.EMPTY_REQUEST)
            }
            Method.METHOD_UPLOAD -> {
                val uploadFile = mUploadFile!!
                val params = mParams!!
                val fileBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                params.forEach { (key, value) -> fileBody.addFormDataPart(key, value) }
                uploadFile.forEach { (key, file) ->
                    fileBody.addFormDataPart(key, file.name, file.asRequestBody("application/octet-stream".toMediaTypeOrNull()))
                }
                return builder.post(fileBody.build())
            }
            Method.METHOD_DOWNLOAD -> {
                val params = mParams
                if (params != null && params.isNotEmpty()) {
                    val formBuilder = FormBody.Builder().apply {
                        params.forEach { (key, value) -> add(key, value) }
                    }
                    return builder.post(formBuilder.build())
                }
                val requestBody = mRequestBody
                if (requestBody != null) {
                    return builder.post(requestBody)
                }
                return builder.get()
            }
            else -> throw IllegalStateException("unsupported request method: $method")
        }
    }

    override fun cancel() {
        clearCall()?.let {
            it.cancel()
            isCanceled = true
        }
    }
}