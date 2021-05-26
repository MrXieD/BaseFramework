package com.example.imlotterytool.repository

import android.content.Context
import android.util.Log
import com.example.imlotterytool.R
import com.example.imlotterytool.db.AppDatabase
import com.example.imlotterytool.db.dao.LotteryDao
import com.example.imlotterytool.db.table.LotteryEntity
import com.example.imlotterytool.network.RetrofitManager
import com.example.imlotterytool.network.entity.LotteryResponse
import com.example.imlotterytool.network.service.LotteryHistoryService
import com.example.imlotterytool.util.*
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet


/**
@date: 2021/5/13
@desription:
 */
class LotteryRepository private constructor(
    private val lotteryDao: LotteryDao,
    private val lotteryHistoryService: LotteryHistoryService
) : ILotteryRepository {

    override fun requestLotteryHistory(
        context: Context,
        lotteryType: String,
        date: String?,
        count: Int
    ): Flow<Resource<LotteryHistory>> {
        return object : DataGetPolicyEx<List<LotteryEntity>, Response<LotteryResponse>, LotteryHistory>() {
            var latestDate = getLatestLotteryDateByType(lotteryType)
            var checkDate: String = date ?: latestDate//如果日期为空则从最近一期开始返回，否则从该日期返回
            override fun db2Result(dbResult: List<LotteryEntity>?): LotteryHistory? {
                if (needPassNum()) {
                    cacheList.sortBy { it.lotteryDate }
                    return LotteryHistory(convertDb2Result(lotteryType, cacheList)!!.toMutableList().also {
                        var i = 0
                        while (i < 49) {
                            it.removeFirst()
                            i++
                        }
                    }, lotteryType)
                } else {
                    dbResult?.let {
                        Log.d(TAG, "db2Result: ")
                        return LotteryHistory(convertDb2Result(lotteryType, it, false), lotteryType)
                    }
                }
                return null
            }

            override suspend fun saveCallResult(netResult: Response<LotteryResponse>?) {
                netResult?.let {
                    if (it.code() == 200) {
                        if (it.isSuccessful) {
                            Log.d(TAG, "saveCallResult: ")
                            val resultBody = it.body()
                            resultBody?.let {
                                if (resultBody.errorCode != 0) {
                                    throw Exception("error:${resultBody.errorCode},and message= ${resultBody.reason.toString()}")
                                }
                            }
                            val list = resultBody?.result?.lotteryResList
                            list?.let {
                                lotteryDao.insertDatas(it@ list)
                                return
                            }
                        } else {
                            throw Exception("error:${it.code()},but ${it.errorBody().toString()}")
                        }
                    } else {
                        throw Exception("error:${it.code()},and message= ${it.errorBody().toString()}")
                    }
                }
            }


            override suspend fun createCall(): Response<LotteryResponse>? {
                Log.d(TAG, "createCall: $checkDate")
                return when (checkDate) {
                    date -> {//应该是已存最近日期和当前最近开奖日期对别
                        lotteryHistoryService.queryHistory(
                            context.resources.getString(R.string.juhe_lottery_key),
                            lotteryType,
                            calRequestPage(date)
                        )
                    }
                    else -> {//直接获取最新数据
                        latestDate = checkDate
                        lotteryHistoryService.queryHistory(
                            context.resources.getString(R.string.juhe_lottery_key),
                            lotteryType,
                            calRequestPage(checkDate)

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

            override suspend fun loadFromDb(date: String?): List<LotteryEntity>? {
                Log.d(TAG, "loadFromDb: ")
                val laseDate = if (date.isNullOrEmpty()) checkDate else date
                checkDate = laseDate
                val list = lotteryDao.getDatasByTypeAndDate(lotteryType, laseDate)?.toMutableList()
                list?.sortBy { it.lotteryDate }
                return list
            }

            override fun needPassNum(): Boolean {
                return when (lotteryType) {
                    LOTTERY_TYPE_FCSD -> true
                    else  ->  false
                }
            }

            private var cacheList: LinkedList<LotteryEntity> = LinkedList<LotteryEntity>()
            override fun needMoreDataToShowMissNum(dbResult: List<LotteryEntity>): Boolean {
                Log.d(TAG, "db2Result: ")
                if (cacheList.isNotEmpty()) {
                    //去除日期相同的
                    cacheList.removeFirst()
                }
                cacheList.addAll(dbResult)
                //更新日期
                checkDate = dbResult.first().lotteryDate
                if(cacheList.size<=50){
                    //只有五十条数量应该是不够的
                    return true
                }
                //应该是对每一位是否在前50条里出现过进行统计，
                //如果没有全部出现那就在获取一次
                when (lotteryType) {
                    LOTTERY_TYPE_FCSD -> {
                        val baiMap = HashSet<Int>()
                        val shiMap = HashSet<Int>()
                        val geMap = HashSet<Int>()
                        cacheList.forEach {
                            val numberArray = convert2Numbers(it.lotteryRes)
                            //百位
                            val bai = numberArray[0]
                            if (!baiMap.contains(bai)) {
                                baiMap.add(bai)
                            }
                            val shi = numberArray[1]
                            if (!shiMap.contains(shi)) {
                                shiMap.add(shi)
                            }
                            val ge = numberArray[2]
                            if (!geMap.contains(ge)) {
                                geMap.add(ge)
                            }
                        }
                        Log.i("needMoreDatam","baiMap.size-->${baiMap.size} ,shiMap.size--->${shiMap.size} ,geMap.size-->${geMap.size} ")
                        return !(baiMap.size == 10 && shiMap.size == 10 && geMap.size == 10)
                    }
                }
                return true
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