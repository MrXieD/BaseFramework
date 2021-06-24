package com.example.contactroom.ui.callrecord.detail.record

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactroom.R
import com.example.contactroom.data.database.entity.CallRecord
import com.example.contactroom.databinding.ItemRecordDetailBinding

/**
@author Anthony.H
@date: 2021/6/24
@desription:
 */
class RecordDetailAdapter :
    ListAdapter<CallRecord, RecordDetailAdapter.RecodDetailViewHolder>(RecordDetailDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecodDetailViewHolder {
        val viewDataBinding: ItemRecordDetailBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_record_detail,
            parent,
            false
        )
        return RecodDetailViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: RecodDetailViewHolder, position: Int) {

    }

    inner class RecodDetailViewHolder(private val viewDataBinding: ViewDataBinding) : RecyclerView.ViewHolder(
        viewDataBinding
            .root
    )

    private class RecordDetailDiffCallBack : DiffUtil.ItemCallback<CallRecord>() {
        override fun areItemsTheSame(oldItem: CallRecord, newItem: CallRecord): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber &&
                    oldItem.recordDate == newItem.recordDate &&
                    oldItem.recordDuration == newItem.recordDuration &&
                    oldItem.recordType == newItem.recordType
        }

        override fun areContentsTheSame(oldItem: CallRecord, newItem: CallRecord): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber &&
                    oldItem.recordDate == newItem.recordDate &&
                    oldItem.recordDuration == newItem.recordDuration &&
                    oldItem.recordType == newItem.recordType
        }

    }
}