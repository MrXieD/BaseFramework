package com.example.imlotterytool.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.imlotterytool.LotteryType
import com.example.imlotterytool.db.table.LotteryItem
import com.example.imlotterytool.repository.LotteryRepository
import com.example.imlotterytool.repository.Resource

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryViewModel(private val context: Context, private val lotteryRepository: LotteryRepository) : ViewModel() {

    private val _fcsdLiveDate = MutableLiveData<String>()


    fun requestFcsdHistory(date: String, count: Int = 50) {
        Log.e("requestFcsdHistory", "requestFcsdHistory:$date ")
        _fcsdLiveDate.value = date
    }


    val fcsdLiveData = _fcsdLiveDate.switchMap {
        Log.e("switchMap", ": ---------->$it")
        lotteryRepository.requestFcsdData(context, it).asLiveData()
    }.map {
        LotteryNotifyEntity(it, LotteryType.FCSD)
    }


}


class LotteryNotifyEntity(val resource: Resource<List<LotteryItem>>, val lotteryType: LotteryType)