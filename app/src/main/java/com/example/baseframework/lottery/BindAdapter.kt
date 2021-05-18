package com.example.baseframework.lottery

import android.util.Log
import androidx.databinding.BindingAdapter
import com.example.baseframework.view.LotteryNumDisplayView
import com.example.imlotterytool.repository.LotteryHistory
import com.example.imlotterytool.repository.Resource
import com.example.imlotterytool.repository.Status
import com.example.imlotterytool.util.getTitleListByLotteryType
import com.google.android.material.navigation.NavigationView

/**
@author Anthony.H
@date 2021/5/15 0015
@description
 */


@BindingAdapter("bindLotteryContent")
fun LotteryNumDisplayView.bindLotteryContent(resource: Resource<LotteryHistory>?) {
    Log.e("bindLotteryContent", "bindLotteryContent: ----->")
    resource?.let { resource ->
        when (resource.status) {
            Status.LOADING
            -> {
            }
            Status.SUCCESS -> {
                val data = resource.data
                data?.let { data ->
                    val dataList = data.list
                    dataList?.let {
                        //显示数据
                        refreshData(dataList, getTitleListByLotteryType(data.lotteryId))
                    }
                }
            }
            Status.ERROR -> {

            }
        }
    }

}

@BindingAdapter("bindSelectListener")
fun NavigationView.bindSelectListener(onNavigationItemReselectedListener: NavigationView.OnNavigationItemSelectedListener?) {
    onNavigationItemReselectedListener?.let {
        setNavigationItemSelectedListener(it)
    }
}