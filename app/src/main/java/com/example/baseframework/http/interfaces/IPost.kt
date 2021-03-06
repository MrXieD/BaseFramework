package com.example.baseframework.http.interfaces

import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnNetCallback

interface IPost {
    /**
     * POST请求
     *
     * @param url      请求地址，该参数不能用空
     * @param params   post提交的参数，该参数不能用空
     * @param listener 回调接口，默认为空
     */
    fun post(url: String, params: HashMap<String, String>, listener: OnNetCallback<Response>? = null): Request<OnNetCallback<Response>, Response>

    /**
     * POST请求
     * @param url       请求地址，该参数不能用空
     * @param param     post提交的JSON参数
     * @param listener  回调接口，默认为空
     */
    fun postJson(url: String, json: String, listener: OnNetCallback<Response>? = null): Request<OnNetCallback<Response>, Response>

    /**
     * POST请求
     * @param url       请求地址，该参数不能用空
     * @param param     post提交的JSON参数
     * @param listener  回调接口，默认为空
     */
    fun postText(url: String, text: String, listener: OnNetCallback<Response>? = null): Request<OnNetCallback<Response>, Response>

    /**
     * POST请求
     *
     * @param url        请求地址，该参数不能用空
     * @param param      post提交的参数类型，该参数不能用空，见`https://tools.ietf.org/html/rfc2045`
     * @param mediaType  post提交的参数，该参数不能用空
     * @param listener   回调接口，默认为空
     */
    fun post(url: String, mediaType: String, param: String, listener: OnNetCallback<Response>? = null): Request<OnNetCallback<Response>, Response>


}