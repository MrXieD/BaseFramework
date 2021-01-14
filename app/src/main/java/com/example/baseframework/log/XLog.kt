package com.example.baseframework.log

import android.text.TextUtils
import android.util.Log
import com.example.baseframework.Constant

object XLog{
    const val TAG="XLog"
    private const val IS_DEBUG = Constant.IS_DEBUG

    fun i(content:String){
        if (IS_DEBUG){
            Log.i(getStackInfo(4, TAG), content)
        }
    }
    fun i(tag:String,content:String){
        if (IS_DEBUG){
            Log.i(getStackInfo(4, tag), content)
        }
    }

    fun w(content:String){
        if (IS_DEBUG){
            Log.w(getStackInfo(4, TAG), content)
        }
    }
    fun w(tag:String,content:String){
        if (IS_DEBUG){
            Log.w(getStackInfo(4, tag), content)
        }
    }
    fun d(content:String){
        if (IS_DEBUG){
            Log.d(getStackInfo(4, TAG), content)
        }
    }
    fun d(tag:String,content:String){
        if (IS_DEBUG){
            Log.d(getStackInfo(4, tag), content)
        }
    }
    fun e(content:String){
        if (IS_DEBUG){
            Log.e(getStackInfo(4, TAG), content)
        }
    }
    fun e(tag:String,content:String){
        if (IS_DEBUG){
            Log.e(getStackInfo(4, tag), content)
        }
    }
    fun v(content:String){
        if (IS_DEBUG){
            Log.v(getStackInfo(4, TAG), content)
        }
    }
    fun v(tag:String,content:String){
        if (IS_DEBUG){
            Log.v(getStackInfo(4, tag), content)
        }
    }

    private fun getStackInfo(stackIndex: Int, tag: String?): String {
        if (tag != null && tag.startsWith("[") && tag.endsWith("]")) return tag
        val buffer = StringBuilder()
        val stackTrace = Thread.currentThread().stackTrace.run { if (stackIndex < size) this[stackIndex] else null }
        buffer.append("[")
        buffer.append(Thread.currentThread().id)
        stackTrace?.let {
            buffer.append(":${it.methodName}(${it.fileName}:${it.lineNumber})")
        }
        if (!TextUtils.isEmpty(tag)) {
            buffer.append(":$tag")
        }
        buffer.append("]")
        return buffer.toString()
    }
}