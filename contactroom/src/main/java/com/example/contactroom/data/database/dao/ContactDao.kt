package com.example.contactroom.data.database.dao

import androidx.room.*
import com.example.contactroom.data.database.entity.*
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

    /**
     * 自定义查询返回字段，一个普通的类(POJO)
     */
    data class CallRecordsResult(
        val count: Int?,
        val date: Long?,
        val number: String?,
        val name: String?
    )


}



