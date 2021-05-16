package com.example.imlotterytool.util

import androidx.databinding.BindingAdapter
import com.example.LotteryNumDisplayView
import com.example.imlotterytool.db.table.LotteryItem
import com.example.imlotterytool.repository.Resource
import com.example.imlotterytool.repository.Status
import com.example.imlotterytool.ui.fragment.LotteryViewModel

/**
@author Anthony.H
@date 2021/5/15 0015
@description
 */
//@BindingAdapter("bindLotteryContent")
//fun LotteryNumDisplayView.bindLotteryContent(lotteryData: List<LotteryItem>, numTextList: List<String>) {
//    refreshData(lotteryData, numTextList)
//}

@BindingAdapter("bindLotteryContent")
fun LotteryNumDisplayView.bindLotteryContent(resource: Resource<List<LotteryItem>>) {
    resource.let {
        when (it.status) {
            Status.LOADING -> {

            }
            Status.SUCCESS -> {
                val data = it.data
                data?.let {
                    //显示数据
                }
            }
            Status.ERROR -> {

            }
        }
    }

}