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

    /**
     *
     * 根据[date]+1的日期来请求3d历史数据
     */
    fun requestFcsdData(context:Context,date: String,count:Int=50): Flow<Resource<List<LotteryItem>>>//


}