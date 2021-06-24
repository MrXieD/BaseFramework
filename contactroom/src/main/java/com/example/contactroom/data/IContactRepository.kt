package com.example.contactroom.data

import com.example.contactroom.data.database.dao.ContactDao
import com.example.contactroom.data.database.entity.CallRecord
import kotlinx.coroutines.flow.Flow

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
interface IContactRepository {

    /**
     *
     * 获取所有通话记录
     */
    fun getAllCallRecords(): Flow<List<ContactDao.CallRecordsResult>>

    /**
     *针对某个人查询对应的所有通话记录
     */
    fun getSigCallRecordsByName(name: String): Flow<List<CallRecord>>

    /**
     * 针对某个号码查询对应的所有通话记录
     */
    fun getSigCallRecordsByNumber(number: String): Flow<List<CallRecord>>

    /**
     * 根据某个人获取所有号码
     */
    fun getNumbersForContact(name: String): Flow<List<String>>

}