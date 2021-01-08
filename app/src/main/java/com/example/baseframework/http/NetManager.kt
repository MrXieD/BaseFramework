package com.example.baseframework.http

import com.example.baseframework.http.interfaces.IFile
import com.example.baseframework.http.interfaces.IGet
import com.example.baseframework.http.interfaces.IPost
import com.example.baseframework.http.okhttp.OKHttpImpl

private val HttpClient = OKHttpImpl()
object NetManager : IGet by HttpClient,IPost by HttpClient,IFile by HttpClient