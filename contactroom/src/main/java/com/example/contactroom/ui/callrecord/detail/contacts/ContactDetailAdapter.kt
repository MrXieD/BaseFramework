package com.example.contactroom.ui.callrecord.detail.contacts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.contactroom.R
import com.example.contactroom.databinding.ItemContactDetailBinding

/**
@author Anthony.H
@date: 2021/6/24
@desription:
 */
class ContactDetailAdapter : ListAdapter<String, ContactDetailAdapter.ContactDetailViewHolder>
    (ContactDetailDiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactDetailViewHolder {
        val dataBinding = DataBindingUtil.inflate<ItemContactDetailBinding>(
            LayoutInflater.from(parent.context), R.layout
                .item_contact_detail, parent,
            false
        )
        return ContactDetailViewHolder(dataBinding)
    }

    override fun onBindViewHolder(holder: ContactDetailViewHolder, position: Int) {

    }

    class ContactDetailViewHolder(viewDataBinding: ItemContactDetailBinding) : RecyclerView.ViewHolder(
        viewDataBinding
            .root
    )

    private class ContactDetailDiffCallBack : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }

}