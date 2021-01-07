package com.example.baseframework.http

import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface HttpClient {
    /**
     * 设置连接超时时间，毫秒
     */
    fun setConnectTimeout(time: Long): HttpClient

    /**
     * 设置读取超时时间，毫秒
     */
    fun setReadTimeout(time: Long): HttpClient

    /**
     * 设置写入超时时间，毫秒
     */
    fun setWriteTimeout(time: Long): HttpClient

    fun setSocketFactory(socketFactory: SocketFactory): HttpClient

    fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory): HttpClient

    fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory, trustManager: X509TrustManager): HttpClient

    fun setHostnameVerifier(hostnameVerifier: HostnameVerifier): HttpClient
}