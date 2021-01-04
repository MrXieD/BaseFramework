package com.example.baseframework.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.example.baseframework.utils.ClassUtil

object ViewBindingUtil {
    @Suppress("UNCHECKED_CAST")
    fun <T : ViewBinding> create(cls: Class<*>, inflater: LayoutInflater): T {
        val bindingClass = ClassUtil.findGenericClass(cls, ViewBinding::class.java)
            ?: throw IllegalArgumentException("Not found ViewBinding class")
        val method = bindingClass.getMethod("inflate", LayoutInflater::class.java)
        return method.invoke(null, inflater) as T
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : ViewBinding> create(
        cls: Class<*>,
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): T {
        val bindingClass = ClassUtil.findGenericClass(cls, ViewBinding::class.java)
            ?: throw IllegalArgumentException("Not found ViewBinding class")

        val method =
            bindingClass.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
        return method.invoke(null, inflater, container, attachToParent) as T
    }
}