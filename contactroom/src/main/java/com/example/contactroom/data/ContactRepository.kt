package com.example.contactroom.data

import android.content.Context
import com.example.contactroom.data.database.ContactDataBase
import com.example.contactroom.data.database.dao.ContactDao
import kotlinx.coroutines.flow.Flow

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class ContactRepository(private val contactDao: ContactDao) : IContactRepository {


    override fun getAllCallRecords(): Flow<List<ContactDao.CallRecordsResult>> {
        return contactDao.getAllCallRecordsRealActive()
    }


}

object ContactRepostoryFactory {

    fun getReposiroty(context: Context): IContactRepository =
        ContactRepository(ContactDataBase.getInstance(context).contactDao())
}