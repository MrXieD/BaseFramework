package com.example.baseframework.utils

import java.io.Closeable

object CloseableUtils {
    fun close(closeable: Closeable){
        try {
            closeable.close()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }
}