package com.example.contactroom.ui.callrecord.detail

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.contactroom.data.database.dao.ContactDao
import com.example.contactroom.ui.callrecord.detail.record.CallRecordDetailFragment
import com.example.contactroom.ui.callrecord.detail.contacts.ContactsDetailFragment

/**
@author Anthony.H
@date: 2021/6/23
@desription:
 */
class CallDetailAdapter(activity: AppCompatActivity, val callRecodsResult: ContactDao.CallRecordsResult) :
    FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = if (callRecodsResult.name == null) 1 else 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                if (itemCount == 1) {
                    CallRecordDetailFragment.createInstance(callRecodsResult.name, callRecodsResult.number)
                } else {
                    ContactsDetailFragment.createInstance(callRecodsResult.name!!)
                }
            }
            1 -> {
                CallRecordDetailFragment.createInstance(callRecodsResult.name, callRecodsResult.number)
            }
            else -> {
                throw IllegalAccessError()
            }

        }
    }
}