package com.example.baseframework.ex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.baseframework.log.XLog

/**
 * 简单的Adapter
 * 用于只显示简单Item
 * test
 */
class SimpleAdapter<T>(private val context: Context, private val layoutResId:Int, private val list:MutableList<T>, val init:(View, T)->Unit) :RecyclerView.Adapter<SimpleAdapter.ItemHolder<T>>(){

    override fun getItemCount(): Int =list.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder<T> {
        val view=LayoutInflater.from(context).inflate(layoutResId,parent,false)
        return ItemHolder(view, init)
    }

    override fun onBindViewHolder(holder: ItemHolder<T>, position: Int) {
        holder.bindView(list[position])
    }

    override fun onBindViewHolder(holder: ItemHolder<T>, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        XLog.i("onBindViewHolder--->payloads")
    }

    class ItemHolder<T>(val view:View,val init:(View,T)-> Unit):RecyclerView.ViewHolder(view){
        fun bindView(item:T){
            init(view,item)
        }
    }

    fun addData(position: Int, data: T) {
        list.add(position, data)
        notifyItemInserted(position)
    }
}