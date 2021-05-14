package com.example.imlotterytool.network.entity

import com.example.imlotterytool.db.table.LotteryEntity
import com.google.gson.annotations.SerializedName

/**
@author Anthony.H
@date: 2021/5/13
@desription:
 */
data class LotteryResponse(@SerializedName("error_code") val errorCode: Int?, val reason: String?, val result: Result?)
data class Result(val lotteryResList: List<LotteryEntity>?, val page: Int, val pageSize: Int, val totalPage: Int) {
}

//data class LotteryData(
//    @SerializedName("lottery_id") val lotteryId: String,
//    @SerializedName("lottery_res") val lotteryRes: String,
//    @SerializedName("lottery_no") val lotteryNo: String,
//    @SerializedName("lottery_date") val lotteryDate: String
//)