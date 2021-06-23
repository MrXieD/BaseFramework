package com.example.contactroom

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.contactroom.data.database.ContactDataBase
import com.example.contactroom.data.database.entity.CallRecord
import com.example.contactroom.data.database.entity.Group
import com.example.contactroom.data.database.entity.User
import com.example.contactroom.data.database.entity.UserGroupCoressRef
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.random.Random

/**
@author Anthony.H
@date: 2021/6/21
@desription:
 */
@RunWith(AndroidJUnit4::class)
class ContactPrePackTest {

    companion object{
        private const val TAG = "ContactPrePackTest"
    }


    @Test
    fun createData() {

        runBlocking {
            val dao = getContactDataBase().contactDao()
            val userIdList = dao.insertUsers(
                listOf(
                    User("张三", "123"),
                    User("张三", "10993289"),
                    User("李四", "132"),
                    User("王五", "231"),
                    User("王五", "1230"),
                    User("二狗子", "2312"),
                    User("王麻子", "2313")
                )
            )

            val groupIdList = dao.insertGroups(
                listOf(
                    Group("气氛组"),
                    Group("干饭组"),
                    Group("牛逼组")
                )
            )


            dao.insertCallRecords(
                listOf(
                    CallRecord("10993289", Date(101), CallRecord.INCOME_CALL, "响铃3秒"),
                    CallRecord("10993289", Date(101), CallRecord.OUT_CALL, "响铃2秒"),
                    CallRecord("10993289", Date(102), CallRecord.OUT_CALL, "未接通"),
                    CallRecord("123", Date(103), CallRecord.INCOME_CALL, "1分16秒"),
                    CallRecord("123", Date(101), CallRecord.INCOME_CALL, "1分13秒"),
                    CallRecord("123", Date(104), CallRecord.INCOME_CALL, "1分16秒"),
                    CallRecord("2312", Date(103), CallRecord.INCOME_CALL, "1分16秒"),
                    CallRecord("2312", Date(103), CallRecord.INCOME_CALL, "2分16秒"),
                    CallRecord("12300000", Date(101), CallRecord.INCOME_CALL, "1分16秒")
                )
            )

            userIdList.forEach { userId ->
                dao.groupAddUser(UserGroupCoressRef(userId.toInt(), groupIdList[Random.nextInt(0, 3)].toInt()))
            }
        }
        Log.e(TAG, "createData: 123" );
    }

    private fun getContactDataBase(): ContactDataBase {
        return Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ContactDataBase::class.java, "pre_pack_contact.db"
        ).build()
    }

}