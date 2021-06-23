package com.example.contactroom.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.contactroom.data.database.dao.ContactDao
import com.example.contactroom.data.database.entity.*

/**
@author Anthony.H
@date: 2021/6/7
@desription:
 */
@Database(
    entities = [User::class, Group::class, UserGroupCoressRef::class, CallRecord::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateConverters::class)
abstract class ContactDataBase : RoomDatabase() {


    abstract fun contactDao(): ContactDao

    companion object {
        const val DATABASE_NAME = "afucontact"
        private lateinit var contactDataBase: ContactDataBase

        fun getInstance(context: Context): ContactDataBase {

            if (!this::contactDataBase.isInitialized) {
                synchronized(this) {
                    if (!this::contactDataBase.isInitialized) {
                        contactDataBase = Room.databaseBuilder(
                            context,
                            ContactDataBase::class.java, DATABASE_NAME
                        ).createFromAsset("pre_pack_contact.db")
                            .build()
                    }
                }
            }
            return contactDataBase
        }
    }


}