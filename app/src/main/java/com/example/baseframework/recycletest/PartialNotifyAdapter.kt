package com.example.baseframework.recycletest

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.baseframework.BR
import com.example.baseframework.R

/**
@author Anthony.H
@date: 2021/5/21
@desription:测试[RecyclerView]局部刷新
 */
class PartialNotifyAdapter(var list: MutableList<String>) : RecyclerView.Adapter<ViewHolder>() {


    companion object {
        private const val TAG = "Adapter"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewDataBinding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_recycle,
            parent,
            false
        )
        return ViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e(TAG, "onBindViewHolder: $position")
        holder.viewDataBinding.let {
            it.setVariable(BR.text, list[position])
            it.setVariable(BR.clickListener, ItemClickWrapper(position))
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        Log.e(
            TAG, "onBindViewHolder-->: ${position};===>,${payloads.isEmpty()}"
        )

        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {//如果payloads不为空，则可以只刷新（绑定值）此item中感兴趣的控件；否则就调用 onBindViewHolder(holder, position)的此item的全量绑定
            holder.viewDataBinding?.let {

                it.setVariable(BR.text, list[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class ItemClickWrapper(private val position: Int) : View.OnClickListener {
        override fun onClick(v: View?) {
            Log.e(TAG, "onClick: $position")
            var old = list!![position].toInt()
            old += 100
            list[position] = old.toString()
            notifyItemChanged(position, position)
        }
    }

}


class ViewHolder(val viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewDataBinding.root)

