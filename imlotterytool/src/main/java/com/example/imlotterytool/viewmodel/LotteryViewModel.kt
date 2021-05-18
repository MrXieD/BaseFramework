package com.example.imlotterytool.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.imlotterytool.repository.ILotteryRepository

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryViewModel(private val context: Context, private val lotteryRepository: ILotteryRepository) : ViewModel() {


    private val _fcsdLiveData = MutableLiveData<String?>()

    private val _lotteryLiveData = MutableLiveData<QueryParam>()

    fun requestFcsdHistory(date: String?, count: Int = 50) {
        _fcsdLiveData.value = date
    }


    fun requestHistory(lotteryId: String, date: String? = null, count: Int = 50) {
        _lotteryLiveData.value = QueryParam(lotteryId, date, count)
    }


//    val fcsdLiveData = _fcsdLiveData.switchMap {
//        lotteryRepository.requestFcsdData(context, it).asLiveData()
//    }.map {
//        LotteryNotifyEntity(it, LotteryType.FCSD)
//    }

    val lotteryLiveData = _lotteryLiveData.switchMap {
        lotteryRepository.requestLotteryHistory(context, it.lotteryId, it.date, it.count).asLiveData()
    }


}

class QueryParam(val lotteryId: String, val date: String?, val count: Int = 50)
