package com.example.roomembedded

import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.roomembedded.database.AnthonyDB
import com.example.roomembedded.database.table.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
@author Anthony.H
@date: 2021/6/9
@desription:
 */
@RunWith(AndroidJUnit4::class)
class RommTest {

    companion object {
        private const val TAG = "RommTest"
    }

    @Before
    fun setUp() {
        runBlocking {
            val idPList = getRoomDb().dbDao().insertPepole(
                listOf(
                    Pepole(0, "张三", Adress("四川", Coordinate("经纬度", 1f, 2f))),
                    Pepole(0, "李四", Adress("贵州", Coordinate("经纬度", 2f, 2f))),
                    Pepole(0, "王五", Adress("湖南", Coordinate("经纬度", 2f, 3f)))
                )
            )

            val idAList = getRoomDb().dbDao().insertAnimal(
                listOf(
                    Animal(0, "大象", AnimalFood("树木", FoodArea("随处", 0f, 0f))),
                    Animal(0, "狮子", AnimalFood("水牛", FoodArea("非洲", 1f, 1f)))
                )
            )

            Log.e(TAG, "setUp: ${idPList.size},${idAList.size}")
            Log.e(TAG, "setUp: ----------------->")
        }
    }

    @After
    fun tearDown() {
        runBlocking {
            getRoomDb().dbDao().clearData()
        }
    }

    @Test
    fun test1() {

        runBlocking {
            val dao = getRoomDb().dbDao()
            dao.getAllPepole().first { list ->
                list.forEach {
                    Log.e(TAG, "$it")
                }
                true
            }

        }
    }

    @Test
    fun test2() {
        runBlocking {
            getRoomDb().dbDao().getAllAnimal().first { list ->
                list.forEach {
                    Log.e(TAG, "animal:$it ")
                }
                true
            }
        }
    }


    private fun getRoomDb(): AnthonyDB {
        return AnthonyDB.getInstance(ApplicationProvider.getApplicationContext())
    }

}