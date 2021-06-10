package com.example.imlotterytool

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.imlotterytool.db.AppDatabase
import com.example.imlotterytool.db.dao.LotteryDao
import com.example.imlotterytool.db.table.LotteryEntity
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
@author Anthony.H
@date: 2021/6/3
@desription:
 */
@RunWith(AndroidJUnit4::class)
class TestAppDatabase {

    private lateinit var appDatabase: AppDatabase
    private lateinit var lotteryDao: LotteryDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        appDatabase = Room.inMemoryDatabaseBuilder(
            context, AppDatabase::class.java
        ).build()
        lotteryDao = appDatabase.lotteryDao()

    }

    @After
    fun closeDb() {
        appDatabase.close()
    }

    @Test
    fun testInsert() {
        val list = listOf<LotteryEntity>(LotteryEntity("2020-09-19", "dlt", "1,2,3", "123"))
        runBlocking {
            lotteryDao.insertDatas(list)
            val result = lotteryDao.getAllDatas()
            assertThat(result!![0].lotteryDate, equalTo("2020-09-1"))
        }
    }


}