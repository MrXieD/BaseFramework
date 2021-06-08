package com.example.contactroom

import android.util.Log
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.contactroom.database.ContactDataBase
import com.example.contactroom.database.entity.CallRecord
import com.example.contactroom.database.entity.Group
import com.example.contactroom.database.entity.User
import com.example.contactroom.database.entity.UserGroupCoressRef
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*
import kotlin.random.Random

/**
@author Anthony.H
@date: 2021/6/7
@desription:

!!!注意，room的dao查询返回的flow默认情况下和一般自定义的flow不同，
room返回的flow在外部collect后是不会自动停止完成的。
 */
@RunWith(AndroidJUnit4::class)
class ContactBaseTest {

    companion object {
        val TESTBBNAME = "test_db"
        private const val TAG = "ContactBaseTest"
    }

    private lateinit var sqliteTestDbHelper: SqliteTestDbOpenHelper

    @Before
    fun setUp() {


        sqliteTestDbHelper = SqliteTestDbOpenHelper(ApplicationProvider.getApplicationContext(), TESTBBNAME)

        SqliteDatabaseTestHelper.createTable(sqliteTestDbHelper)
        runBlocking {
            val dao = getContactDataBase().contactDao()
            val userIdList = dao.insertUsers(
                listOf(
                    User(null, "张三", "123"),
                    User(null, "张三", "10993289"),
                    User(null, "李四", "132"),
                    User(null, "王五", "231"),
                    User(null, "王五", "1230"),
                    User(null, "二狗子", "2312"),
                    User(null, "王麻子", "2313")
                )
            )

            val groupIdList = dao.insertGroups(
                listOf(
                    Group(null, "气氛组"),
                    Group(null, "干饭组"),
                    Group(null, "牛逼组")
                )
            )


            dao.insertCallRecords(
                listOf(
                    CallRecord(null, "10993289", Date(101), CallRecord.INCOME_CALL, "响铃3秒"),
                    CallRecord(null, "10993289", Date(101), CallRecord.OUT_CALL, "响铃2秒"),
                    CallRecord(null, "10993289", Date(102), CallRecord.OUT_CALL, "未接通"),
                    CallRecord(null, "123", Date(103), CallRecord.INCOME_CALL, "1分16秒"),
                    CallRecord(null, "123", Date(101), CallRecord.INCOME_CALL, "1分13秒"),
                    CallRecord(null, "123", Date(104), CallRecord.INCOME_CALL, "1分16秒"),
                    CallRecord(null, "2312", Date(103), CallRecord.INCOME_CALL, "1分16秒"),
                    CallRecord(null, "2312", Date(103), CallRecord.INCOME_CALL, "2分16秒"),
                    CallRecord(null, "12300000", Date(101), CallRecord.INCOME_CALL, "1分16秒")
                )
            )

            userIdList.forEach { userId ->
                dao.groupAddUser(UserGroupCoressRef(userId.toInt(), groupIdList[Random.nextInt(0, 3)].toInt()))
            }
        }
    }

    @After
    fun tearDown() {
        SqliteDatabaseTestHelper.clearDatabase(sqliteTestDbHelper)
    }


    @Test
    fun test1() {
        runBlocking {
            val dao = getContactDataBase().contactDao()
            val userWithGroups = dao.getUsersWithGoroups()
            val stringBuilder = StringBuilder()
            userWithGroups.first { list ->
                list.forEach { userWithGroups ->
                    stringBuilder.clear()
                    stringBuilder.append(userWithGroups.user.userName)
                        .append("组有:")
                    userWithGroups.groupList.forEach { group ->
                        stringBuilder
                            .append(group.groupName)
                            .append(" ")
                    }
                    Log.e(TAG, "->: ${stringBuilder.toString()}")
                }
                true//因为room的flow不会自动关闭，这里需要这样操作
            }
        }
    }


    @Test
    fun test2() {
        runBlocking {
            val dao = getContactDataBase().contactDao()
            val list = dao.getAllCallRecordsReal()
            list.forEach { callRecordsResult ->
                Log.e(TAG, "test2: ${callRecordsResult.count}")
            }
        }
    }

    @Test
    fun test3() {
        runBlocking {
            val dao = getContactDataBase().contactDao()
            dao.getAllCallRecordsRealActive().first {
                it.forEach { result ->
                    Log.e(TAG, "test3: ${result.count}")
                }
                true
            }
        }
    }

    @Test
    fun test4() {

    }


    private fun getContactDataBase(): ContactDataBase {
        val contactDataBase = Room.databaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ContactDataBase::class.java, TESTBBNAME
        ).build()
        return contactDataBase
    }

}