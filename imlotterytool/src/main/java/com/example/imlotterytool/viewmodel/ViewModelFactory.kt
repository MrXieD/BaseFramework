package com.example.imlotterytool.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.imlotterytool.repository.LotteryRepositoryImpl

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryViewModelFactory(private val context: Context, private val lotteryRepositoryImpl: LotteryRepositoryImpl) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LotteryViewModel(context,lotteryRepositoryImpl) as T
    }


}