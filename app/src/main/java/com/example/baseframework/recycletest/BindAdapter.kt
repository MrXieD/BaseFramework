package com.example.baseframework.recycletest

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView

/**
@author Anthony.H
@date: 2021/5/21
@desription:
 */
@BindingAdapter("bindAdapter")
fun RecyclerView.bindAdapter(adapter: PartialNotifyAdapter) {
    this.adapter = adapter
}