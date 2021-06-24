package com.example.contactroom.ui.callrecord.detail

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.example.contactroom.R
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
        private const val TAG = "CallRecordDetailActivit"
        const val CALLRECODS_KEY = "CALLRECODS_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCallrecordDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }


    private fun init() {
        setSupportActionBar(binding.topToolbar)
        binding.bottomToolbar.setOnMenuItemClickListener(menuItemClickListener)

        val callRecodsResult = intent.getParcelableExtra<ContactDao.CallRecordsResult>(CALLRECODS_KEY)
        callRecodsResult?.let {
            binding.pager.adapter = CallDetailAdapter(this, callRecodsResult)
            callRecodsResult.name?.let {
                binding.tabLayout.visibility = View.VISIBLE
                TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                    tab.text = if (position == 0) {
                        "详情"
                    } else {
                        "通话记录"
                    }
                }.attach()
            }
            binding.pager.registerOnPageChangeCallback(pagerChangeListener)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)

    }

    /**
     *
     * 页面滑动监听器
     */
    private val pagerChangeListener = object : ViewPager2.OnPageChangeCallback() {

        override fun onPageSelected(position: Int) {
            Log.e(TAG, "onPageSelected: $position")

            when (position) {
                0 -> {
                    binding.bottomActionmv.menu.apply {
                        binding.bottomToolbar.post {
                            clear()
                            menuInflater.inflate(R.menu.menu_contacts_detail, this)
                        }
                    }
                }
                1 -> {
                    binding.bottomActionmv.menu.apply {
                        clear()
                        menuInflater.inflate(R.menu.menu_delete_records, this)
                    }
                }
            }
        }
    }


    /**
     * 底部toolbar菜单选择监听
     */
    private val menuItemClickListener = Toolbar.OnMenuItemClickListener { true }


    override fun onDestroy() {
        super.onDestroy()
        binding.pager.unregisterOnPageChangeCallback(pagerChangeListener)
    }


}