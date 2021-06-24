package com.example.contactroom.ui.callrecord

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.contactroom.ui.callrecord.child.allrecords.AllCallRecordsFragment
import com.example.contactroom.ui.callrecord.child.missrecords.MissedCallRecordsFragment

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class CallAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                AllCallRecordsFragment.createInstance()
            }
            1 -> {
                MissedCallRecordsFragment.createInstance()
            }
            else -> {
                throw IllegalAccessError()
            }
        }
    }
}