package com.example.contactroom.ui.call.child.allrecords

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactroom.BR
import com.example.contactroom.R
import com.example.contactroom.data.database.dao.ContactDao
import com.example.contactroom.databinding.ItemAllCallrecordsBinding
import com.example.contactroom.ui.call.CallFragmentDirections

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class AllCallRecordsListAdapter :
    ListAdapter<ContactDao.CallRecordsResult, AllCallRecordsListAdapter.AllRecordsViewHolder>(AllRecordsDiffCallBack()) {

    companion object {
        private const val TAG = "AllCallRecordsListAdapt"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllRecordsViewHolder {
        val viewDataBinding: ItemAllCallrecordsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_all_callrecords,
            parent,
            false
        )
        return AllRecordsViewHolder(viewDataBinding)

    }

    override fun onBindViewHolder(holder: AllRecordsViewHolder, position: Int) {
        holder.viewDataBinding.apply {
            callrecord = getItem(position)
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


    inner class AllRecordsViewHolder(val viewDataBinding: ItemAllCallrecordsBinding) : RecyclerView.ViewHolder(
        viewDataBinding
            .root
    ) {
        init {
            viewDataBinding.clickListener = detailClickListener
        }
    }

    private val detailClickListener = View.OnClickListener {
        //根据号码跳转通话记录详情页面
        val pos = (it.parent.parent as RecyclerView).getChildAdapterPosition(it.parent as View)

        Log.i(TAG, "detail click $pos ")
        val callRecordResult = getItem(pos)
        val direction = CallFragmentDirections.actionNavigationCallToNavigationContactDetail()
        it.findNavController().navigate(direction)
    }


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

