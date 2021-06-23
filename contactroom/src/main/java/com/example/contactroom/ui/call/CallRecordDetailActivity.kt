package com.example.contactroom.ui.call

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.contactroom.data.database.dao.ContactDao
import com.example.contactroom.databinding.ActivityCallrecordDetailBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
@author Anthony.H
@date: 2021/6/23
@desription:通话记录/联系人 详情页面
 */
class CallRecordDetailActivity : AppCompatActivity() {


    private lateinit var binding: ActivityCallrecordDetailBinding

    companion object {
        const val CALLRECODS_KEY = "CALLRECODS_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallrecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()

    }

    private fun init() {
        val callRecodsResult = intent.getParcelableExtra<ContactDao.CallRecordsResult>(CALLRECODS_KEY)
        callRecodsResult?.let {
            binding.pager.adapter = CallDetailAdapter(this, callRecodsResult)
            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = if (position == 0) {
                    "全部通话"
                } else {
                    "未接来电"
                }
            }.attach()
        }
    }

}