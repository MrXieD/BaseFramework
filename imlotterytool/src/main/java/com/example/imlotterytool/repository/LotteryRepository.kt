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


/**
@date: 2021/5/13
@desription:
 */
class LotteryRepository private constructor(
    private val lotteryDao: LotteryDao,
    private val lotteryHistoryService: LotteryHistoryService
) :
    ILotteryRepository {


    /**
     *
     *
     * [date]参数为空：
     *      1、昨天数据没有，直接网络获取昨天起的一批数据，再从数据库返回一批
     *       2、昨天的数据有，直接从数据库返回一批数据
     * [date]参数不为空：（对于UI来说，应该请求的是当前期以后的那一期）
     *       1、[date]期数据存在，直接返回从此起以前的一批数据
     *       2、[date]期数据不存在，网络获取[date]起的一批数据，再从数据库返回一批
     *
     *
     */
    override fun requestFcsdData(context: Context, date: String, count: Int): Flow<Resource<List<LotteryItem>>> {

        Log.e(TAG, "requestFcsdData: ")
        return object : DataGetPolicy<List<LotteryItem>, Response<LotteryResponse>, List<LotteryEntity>>() {
            var checkDate: String = if (date == "null") getLatestFcsdDate() else date//如果日期为空则从最近一期开始返回，否则从该日期返回

            override suspend fun saveCallResult(item: List<LotteryEntity>) {
                item?.let {
                    lotteryDao.insertDatas(item)
                }
            }

            override fun shouldFetch(data: List<LotteryEntity>?): Boolean {
                return null == data || data.isEmpty()
            }


            override suspend fun loadFromDb(): List<LotteryEntity>? {
                return lotteryDao.getDatasOverDate(checkDate)
            }

            override suspend fun createCall(): Response<LotteryResponse> {
                Log.e(TAG, "createCall: ${checkDate}")
                return when (checkDate) {
                    date -> {
                        lotteryHistoryService.queryHistory(
                            context.resources.getString(R.string.juhe_lottery_key),
                            LOTTERY_TYPE_FCSD, calRequestPage(date!!)
                        )

                    }
                    else -> {
                        lotteryHistoryService.queryHistory(
                            context.resources.getString(R.string.juhe_lottery_key),
                            LOTTERY_TYPE_FCSD
                        )
                    }
                }
            }

            override suspend fun net2Db(netResult: Response<LotteryResponse>?): List<LotteryEntity>? {
                netResult?.let {
                    return it.body()!!.result!!.lotteryResList
                }
                return null
            }

            override suspend fun db2Result(dbResult: List<LotteryEntity>?): List<LotteryItem>? {
                dbResult?.let {
                    return convert2FcsdDBData(dbResult)
                }
                return null
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