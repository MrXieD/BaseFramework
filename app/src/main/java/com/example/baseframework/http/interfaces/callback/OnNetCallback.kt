package com.example.baseframework.http.interfaces.callback

import com.example.baseframework.http.Response

interface OnNetCallback<SUCCESS> {
    fun onSuccess(result:SUCCESS)
    fun onFailure(error: Response)
}