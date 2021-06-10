package com.example.contactroom.data

import com.example.contactroom.data.database.dao.ContactDao
import kotlinx.coroutines.flow.Flow

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class ContactRepository : IContactRepository {


    override fun getAllCallRecords(): Flow<List<ContactDao.CallRecordsResult>> {

    }

}