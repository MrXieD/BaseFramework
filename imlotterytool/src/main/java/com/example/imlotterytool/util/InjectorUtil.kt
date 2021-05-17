package com.example.imlotterytool.util

import android.content.Context
import com.example.imlotterytool.repository.LotteryRepository
import com.example.imlotterytool.ui.LotteryViewModelFactory

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
object InjectorUtil {

    private fun getLotteryRepositery(context: Context): LotteryRepository {

        return LotteryRepository.getInstance(context)
    }


    fun getLotteryViewModelFatory(context: Context) = LotteryViewModelFactory(context,getLotteryRepositery(context))


}