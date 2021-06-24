package com.example.contactroom.data.database.dao

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.example.contactroom.data.database.entity.*
import com.example.contactroom.util.DateUtil
import kotlinx.coroutines.flow.Flow

/**
@author Anthony.H
@date: 2021/6/7
@desription:
 */
@Dao
abstract class ContactDao {


    @Transaction
    @Query("SELECT * FROM user")
    abstract fun getUsersWithGoroups(): Flow<List<UserWithGroups>>


    @Transaction
    @Query("SELECT * FROM `group`")
    abstract fun getGoroupsWithUsers(): Flow<List<GroupWithUsers>>

    @Insert
    abstract suspend fun insertUsers(users: List<User>): List<Long>

    @Insert
    abstract suspend fun insertGroups(grops: List<Group>): List<Long>

    @Insert
    abstract suspend fun groupAddUser(userGroupCoressRef: UserGroupCoressRef)


    @Delete
    abstract suspend fun deleteUsersOnly(users: List<User>)


    @Query("DELETE FROM  user_group_cross_ref WHERE user_id =:userId")
    abstract suspend fun moveUserFromGroup(userId: Int)

    @Insert
    abstract suspend fun insertCallRecords(callRecords: List<CallRecord>)


    @Query("select  count(*)as count,cr.record_date as date, cr.phone_number as number,   (select  u.user_name from user as u where u.phone_numbers=cr.phone_number )  as name from call_record as cr group by cr.phone_number,cr.record_date")
    abstract suspend fun getAllCallRecordsReal(): List<CallRecordsResult>

    /**
     * 记住，返回值是Flow和LiveData的一定不要加suspend，否则编译不通过
     */
    @Query("select  count(*)as count,cr.record_date as date, cr.phone_number as number,   (select  u.user_name from user as u where u.phone_numbers=cr.phone_number )  as name from call_record as cr group by cr.phone_number,cr.record_date")
    abstract fun getAllCallRecordsRealActive(): Flow<MutableList<CallRecordsResult>>


    @Query("select call_record.* from call_record,user where call_record.phone_number=user.phone_numbers and user.user_name=:name order by call_record.record_date desc")
    abstract fun getSigCallRecodsByName(name: String): Flow<List<CallRecord>>

    @Query("select * from call_record where call_record.phone_number=:number order by call_record.record_date desc")
    abstract fun getSigCallRecodsByNumber(number: String): Flow<List<CallRecord>>

    /**
     * 先删除用户表中记录，
     * 然后删除对应的[CallRecord]中的记录
     * 删除组中成员
     */
    @Transaction
    open suspend fun deleteUsers(users: List<User>) {
        users.forEach { user ->
            deleteUsers(listOf(user))//删除用户表记录
            moveUserFromGroup(user.userId!!)  //删除组成员表
        }
    }

    @Query("select user.phone_numbers from user where user.user_name=:name")
    abstract fun getNumbersForContact(name: String): Flow<List<String>>

    /**
     * 自定义查询返回字段，一个普通的类(POJO)
     */
    data class CallRecordsResult(
        val count: Int?,
        val date: Long?,
        val number: String?,
        val name: String?
    ) : Parcelable {
        /**
         * 格式化后的日期
         */
        var formatDate: String? = number

        constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Long::class.java.classLoader) as? Long,
            parcel.readString(),
            parcel.readString()
        ) {
            formatDate = parcel.readString()
        }

        init {
            date?.let {
                formatDate = DateUtil.timeStamp2FormatDate(it)
            }
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeValue(count)
            parcel.writeValue(date)
            parcel.writeString(number)
            parcel.writeString(name)
            parcel.writeString(formatDate)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<CallRecordsResult> {
            override fun createFromParcel(parcel: Parcel): CallRecordsResult {
                return CallRecordsResult(parcel)
            }

            override fun newArray(size: Int): Array<CallRecordsResult?> {
                return arrayOfNulls(size)
            }
        }


    }


}



