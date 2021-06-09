package com.example.roomembedded.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.roomembedded.database.dao.DBDao
import com.example.roomembedded.database.table.Animal
import com.example.roomembedded.database.table.DataConverters
import com.example.roomembedded.database.table.Pepole

/**
@author Anthony.H
@date: 2021/6/9
@desription:
 */
@Database(entities = [Pepole::class, Animal::class], version = 1, exportSchema = false)
@TypeConverters(DataConverters::class)
abstract class AnthonyDB : RoomDatabase() {

    abstract fun dbDao(): DBDao


    companion object {
        const val DATABASE_NAME = "AnthonyDB"
        private lateinit var db: AnthonyDB

        fun getInstance(context: Context): AnthonyDB {

            if (!this::db.isInitialized) {
                synchronized(this) {
                    if (!this::db.isInitialized) {
                        db = Room.databaseBuilder(
                            context,
                            AnthonyDB::class.java, DATABASE_NAME
                        ).build()
                    }
                }
            }
            return db
        }
    }


}