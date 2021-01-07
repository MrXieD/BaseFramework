package com.example.baseframework.http

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.Reader
import kotlin.jvm.Throws

interface Response :Closeable{
    fun isSuccessful(): Boolean

    fun isCanceled(): Boolean

    fun getException(): Exception?

    fun getHeaders(): HashMap<String, ArrayList<String>>?

    fun getBodyLength(): Long

    fun getBody(defBody: String = ""): String

    fun getBodyString(): String?

    fun getBodyBytes(): ByteArray?

    @Throws(IOException::class)
    fun getBodyOfByteStream(action: (InputStream?) -> Unit)

    @Throws(IOException::class)
    fun getBodyOfCharStream(action: (Reader?) -> Unit)

    fun getResponseCode(): Int

    fun getMessage(): String

    fun getProtocol(): String

    fun getUrl(): String

}