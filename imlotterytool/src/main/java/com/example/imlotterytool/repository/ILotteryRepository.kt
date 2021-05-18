package com.example.imlotterytool.repository

import android.content.Context
import com.example.imlotterytool.db.table.LotteryItem
import kotlinx.coroutines.flow.Flow

/**
@author Anthony.H
@date: 2021/5/13
@desription:
 */
interface ILotteryRepository {

    fun requestLotteryHistory(
        context: Context,
        lotteryId: String,
        date: String?,
        count: Int = 50
    ): Flow<Resource<LotteryHistory>>

}


class LotteryHistory(val list: List<LotteryItem>?, val lotteryId: String)