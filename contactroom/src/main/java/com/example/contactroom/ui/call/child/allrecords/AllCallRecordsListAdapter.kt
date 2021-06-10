package com.example.contactroom.ui.call.child.allrecords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.contactroom.R

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class AllCallRecordsListAdapter : RecyclerView.Adapter<AllCallRecordsListAdapter.AllRecordsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRecordsViewHolder {
        val viewDataBinding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_all_callrecords,
            parent,
            false
        )
        return AllRecordsViewHolder(viewDataBinding)

    }

    override fun onBindViewHolder(holder: AllRecordsViewHolder, position: Int) {
        holder.viewDataBinding.let {

        }
    }

    override fun onBindViewHolder(
        holder: AllRecordsViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            //todo 个别控件值改变
        }
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }


    inner class AllRecordsViewHolder(val viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(
        viewDataBinding
            .root
    )

}

