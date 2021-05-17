package com.example.imlotterytool.ui.fragment

import android.content.Context
import androidx.lifecycle.*
import com.example.imlotterytool.db.table.LotteryItem
import com.example.imlotterytool.repository.LotteryRepository
import com.example.imlotterytool.repository.Resource

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryViewModel(private val context: Context, private val lotteryRepository: LotteryRepository) : ViewModel() {

    private val _fcsdLiveDate = MutableLiveData<String?>()


    fun requestFcsdHistory(date: String?, count: Int = 50) {
        _fcsdLiveDate.value = date
    }


    val fcsdLiveData: LiveData<Resource<List<LotteryItem>>> = _fcsdLiveDate.switchMap {
        lotteryRepository.requestFcsdData(context, it).asLiveData()
    }

}