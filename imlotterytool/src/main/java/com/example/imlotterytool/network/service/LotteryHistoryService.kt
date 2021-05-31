package com.example.imlotterytool.network.service

import com.example.imlotterytool.network.entity.LotteryResponse
import com.example.imlotterytool.util.PAGE_SIZE
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
@author Anthony.H
@date: 2021/5/13
@desription:
 */
interface LotteryHistoryService {

    @GET("lottery/history")
    suspend fun queryHistory(
        @Query("key") key: String,
        @Query("lottery_id") lotteryId: String,
        @Query("page") page: String="1",
        @Query(" page_size") pageSize: String = PAGE_SIZE.toString()
    ): Response<LotteryResponse>
}