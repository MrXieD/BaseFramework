package com.example.imlotterytool.util

import android.content.Context
import com.example.imlotterytool.repository.LotteryRepositoryImpl
import com.example.imlotterytool.viewmodel.LotteryViewModelFactory

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
object InjectorUtil {


    private fun getLotteryRepositery(context: Context): LotteryRepositoryImpl {
        return LotteryRepositoryImpl.getInstance(context)
    }


    fun getLotteryViewModelFatory(context: Context) =
        LotteryViewModelFactory(context, getLotteryRepositery(context))


}