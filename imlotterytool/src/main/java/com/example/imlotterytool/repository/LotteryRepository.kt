package com.example.imlotterytool.repository

import com.example.imlotterytool.db.dao.LotteryDao
import com.example.imlotterytool.db.table.LotteryEntity
import com.example.imlotterytool.db.table.LotteryItem
import com.example.imlotterytool.network.entity.LotteryResponse
import com.example.imlotterytool.network.service.LotteryHistoryService
import com.example.imlotterytool.util.convert2FcsdDBData
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

/**
 *
 *
 *
@date: 2021/5/13
@desription:
 */

class LotteryRepository(private val lotteryDao: LotteryDao, private val lotteryHistoryService: LotteryHistoryService) :
    ILotteryRepository {
    /**
     * 先从数据库查找是否有日期为[date]+1以上的数据，
     *
     * 如果有则返回，否则从网络获取
     *
     * 1、首先检查是否有当天[date]数据，如果有，则返回当前起向后的最多50条数据
     * 如果没有且数据库总条数等于0说明还没有数据，那么直接请求
     *
     *
     */
    override fun requestFcsdData(date: String, count: Int): Flow<Resource<List<LotteryItem>>> {

        return object : DataGetPolicy<List<LotteryItem>, Response<LotteryResponse>, List<LotteryEntity>>() {

            override suspend fun saveCallResult(item: List<LotteryEntity>) {
                item.let {
                    lotteryDao.insertDatas(item)
                }
            }

            override fun shouldFetch(data: List<LotteryEntity>?): Boolean {
                return null == data
            }


            override suspend fun loadFromDb(): List<LotteryEntity>? {
                return lotteryDao.getDatasByOverDate(date)
            }

            override suspend fun createCall(): Response<LotteryResponse> {
                return lotteryHistoryService.queryHistory("key", "fcsd", "page", "50")
            }

            override suspend fun net2Db(netResult: Response<LotteryResponse>?): List<LotteryEntity>? {
                netResult?.let {
                    return it.body()!!.result!!.lotteryResList
                }
                return null
            }

            override suspend fun db2Result(dbResult: List<LotteryEntity>?): List<LotteryItem>? {
                dbResult?.let {
                    convert2FcsdDBData(dbResult, lotteryDao.getDataByDate(date))
                }
                return null
            }

        }.flow
    }
}