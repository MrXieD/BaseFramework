package com.example.contactroom.ui.call.child.allrecords

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactroom.R
import com.example.contactroom.data.database.dao.ContactDao

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class AllCallRecordsListAdapter :
    ListAdapter<ContactDao.CallRecordsResult, AllCallRecordsListAdapter.AllRecordsViewHolder>(AllRecordsDiffCallBack()) {


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


    inner class AllRecordsViewHolder(val viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(
        viewDataBinding
            .root
    )


    private class AllRecordsDiffCallBack : DiffUtil.ItemCallback<ContactDao.CallRecordsResult>() {
        override fun areItemsTheSame(
            oldItem: ContactDao.CallRecordsResult,
            newItem: ContactDao.CallRecordsResult
        ): Boolean {
            return oldItem.count == newItem.count && oldItem.date == newItem.date && oldItem.name == newItem.name &&
                    oldItem
                        .number == newItem.number
        }

        override fun areContentsTheSame(
            oldItem: ContactDao.CallRecordsResult,
            newItem: ContactDao.CallRecordsResult
        ): Boolean {
            return oldItem.count == newItem.count && oldItem.date == newItem.date && oldItem.name == newItem.name &&
                    oldItem
                        .number == newItem.number
        }

    }

}

