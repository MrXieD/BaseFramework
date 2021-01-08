package com.example.baseframework.http

import javax.net.SocketFactory
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

interface HttpClientBuilder {
    fun setConnectTimeout(time: Long): HttpClientBuilder

    fun setReadTimeout(time: Long): HttpClientBuilder

    fun setWriteTimeout(time: Long): HttpClientBuilder

    fun setSocketFactory(socketFactory: SocketFactory): HttpClientBuilder

    fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory): HttpClientBuilder

    fun setSslSocketFactory(sslSocketFactory: SSLSocketFactory, trustManager: X509TrustManager): HttpClientBuilder

    fun setHostnameVerifier(hostnameVerifier: HostnameVerifier): HttpClientBuilder
}