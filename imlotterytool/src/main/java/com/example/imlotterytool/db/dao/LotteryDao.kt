package com.example.imlotterytool.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.imlotterytool.db.table.LotteryEntity

/**
@author Anthony.H
@date: 2021/5/13
@desription:
 */

@Dao
abstract class LotteryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDatas(fcsdTable: List<LotteryEntity>)

    @Query("SELECT * FROM LotteryEntity ORDER BY lottery_date ASC")
    abstract suspend fun getAllDatas(): List<LotteryEntity>?


    //
    @Query("SELECT * FROM LotteryEntity WHERE lottery_date <=:lotteryDate ORDER BY lottery_date ASC")
    abstract suspend fun getDatasOverDate(lotteryDate: String?): List<LotteryEntity>?


    /**
     *
     * 查询日期大于[lotteryDate]的数据
     * 如果日期为空则表示返回全部数据，
     * 否则返回大于该日期的数据
     *
     * @see [getDatasOverDate]
     */
    suspend fun getDatasByOverDate(lotteryDate: String?): List<LotteryEntity>? {
        return when (lotteryDate) {
            null -> {
                getAllDatas()
            }
            else -> {
                getDatasOverDate(lotteryDate)
            }
        }
    }

    /**
     * 根据日期返回上一期
     */
    @Query("SELECT * FROM LotteryEntity WHERE lottery_date=(:lotteryDate) ORDER BY lottery_date ASC")
    abstract suspend fun getDataByDate(lotteryDate: String): LotteryEntity?


    @Query("SELECT * FROM LotteryEntity WHERE lottery_id=:lotteryId AND  lottery_date <=:lotteryDate ORDER BY lottery_date ASC")
    abstract suspend fun getDatasByTypeAndDate(lotteryId: String, lotteryDate: String?): List<LotteryEntity>?

}