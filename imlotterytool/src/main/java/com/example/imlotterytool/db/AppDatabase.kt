package com.example.imlotterytool.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.imlotterytool.network.entity.LotteryData

/**
@author Anthony.H
@date: 2021/1/21 0021
@desription:
 */

@Database(entities = [], version = 1)
abstract class AppDatabase : RoomDatabase() {


    abstract fun weatherDao(): LotteryData


    companion object {
        const val DATABASE_NAME = "afuweather"

        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, DATABASE_NAME
                ).build()
                INSTANCE = db
                db
            }
        }
    }


}