package com.example.baseframework.lottery

import android.util.Log
import androidx.databinding.BindingAdapter
import com.example.baseframework.view.LotteryNumDisplayView
import com.example.imlotterytool.repository.Status
import com.example.imlotterytool.util.getTitleListByLotteryType
import com.example.imlotterytool.viewmodel.LotteryNotifyEntity

/**
@author Anthony.H
@date 2021/5/15 0015
@description
 */


@BindingAdapter("bindLotteryContent")
fun LotteryNumDisplayView.bindLotteryContent(lotteryNotifyEntity: LotteryNotifyEntity?) {
    Log.e("bindLotteryContent", "bindLotteryContent: ----->")
    lotteryNotifyEntity?.let {
        when (it.resource.status) {
            Status.LOADING -> {

            }
            Status.SUCCESS -> {
                val data = it.resource.data
                data?.let {
                    //显示数据
                    refreshData(it@ data, getTitleListByLotteryType(it@ lotteryNotifyEntity.lotteryType))
                }
            }
            Status.ERROR -> {

            }
        }
    }

}