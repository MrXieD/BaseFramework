package com.example.imlotterytool.repository

import android.content.Context
import android.util.Log
import com.example.imlotterytool.R
import com.example.imlotterytool.db.AppDatabase
import com.example.imlotterytool.db.dao.LotteryDao
import com.example.imlotterytool.db.table.LotteryEntity
import com.example.imlotterytool.db.table.LotteryItem
import com.example.imlotterytool.network.RetrofitManager
import com.example.imlotterytool.network.entity.LotteryResponse
import com.example.imlotterytool.network.service.LotteryHistoryService
import com.example.imlotterytool.util.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.util.*


/**
@date: 2021/5/13
@desription:
 */
class LotteryRepository private constructor(
    private val lotteryDao: LotteryDao,
    private val lotteryHistoryService: LotteryHistoryService
) :
    ILotteryRepository {

    override fun requestLotteryHistory(
        context: Context,
        lotteryId: String,
        date: String?,
        count: Int
    ): Flow<Resource<LotteryHistory>> {
        return object : DataGetPolicyEx<List<LotteryEntity>, Response<LotteryResponse>, LotteryHistory>() {
            var latestDate = getLatestFcsdDate()
            var checkDate: String = date ?: latestDate//如果日期为空则从最近一期开始返回，否则从该日期返回
            override fun db2Result(dbResult: List<LotteryEntity>?): LotteryHistory? {
                dbResult?.let {
                    Log.d(TAG, "db2Result: ")
                    return LotteryHistory(convertDb2Result(lotteryId, it), lotteryId)
                }
                return null
            }

            override suspend fun saveCallResult(netResult: Response<LotteryResponse>?) {
                netResult?.let {
                    Log.d(TAG, "saveCallResult: ")
                    val list = it.body()?.result?.lotteryResList
                    list?.let {
                        lotteryDao.insertDatas(it@ list)
                    }
                }
            }


            override suspend fun createCall(): Response<LotteryResponse>? {
                Log.d(TAG, "createCall: $checkDate")
                return when (checkDate) {
                    date -> {//应该是已存最近日期和当前最近开奖日期对别
                        lotteryHistoryService.queryHistory(
                            context.resources.getString(R.string.juhe_lottery_key),
                            lotteryId, calRequestPage(date!!)
                        )
                    }
                    else -> {//直接获取最新数据
                        lotteryHistoryService.queryHistory(
                            context.resources.getString(R.string.juhe_lottery_key),
                            lotteryId
                        )
                    }
                }
            }

            override fun shouldFetch(dbResult: List<LotteryEntity>?): Boolean {
                Log.d(TAG, "shouldFetch: ")
                return when (dbResult) {
                    null -> {
                        true
                    }

                    else -> {
                        if (dbResult.isEmpty()) {
                            return true
                        }
                        when (checkDate) {
                            latestDate -> {//如果是查询最新数据，保证数据库最新该类型数据和最近开奖日期一样
                                dbResult.last().lotteryDate != latestDate
                            }
                            else -> { //如果是查询指定日期，只需要保证返回的数据不为空即可
                                true
                            }
                        }
                    }
                }
            }

            override suspend fun loadFromDb(): List<LotteryEntity>? {
                Log.d(TAG, "loadFromDb: ")
                return lotteryDao.getDatasByTypeAndDate(lotteryId, checkDate)
            }
        }.flow
    }


    companion object {
        private const val TAG = "LotteryRepository"
        private var instance: LotteryRepository? = null
        fun getInstance(context: Context): LotteryRepository {
            if (instance == null) {
                synchronized(LotteryRepository::class.java) {
                    if (instance == null) {
                        instance = LotteryRepository(
                            AppDatabase.getInstance(context).weatherDao(),
                            RetrofitManager.createService(clazz = LotteryHistoryService::class.java)
                        )
                    }
                }
            }
            return instance!!
        }
    }
}