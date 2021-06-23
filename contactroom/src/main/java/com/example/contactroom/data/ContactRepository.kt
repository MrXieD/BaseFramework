package com.example.contactroom.data

import android.content.Context
import com.example.contactroom.data.database.ContactDataBase
import com.example.contactroom.data.database.dao.ContactDao
import com.example.contactroom.data.database.entity.CallRecord
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

    override fun getSigCallRecordsByName(name: String): Flow<List<CallRecord>> {
        TODO("Not yet implemented")
    }

    override fun getSigCallRecordsByNumber(number: String): Flow<List<CallRecord>> {
        TODO("Not yet implemented")
    }


}

object ContactRepostoryFactory {


    private lateinit var conRepository: IContactRepository

    fun getReposiroty(context: Context): IContactRepository {
        if (!this::conRepository.isInitialized) {
            conRepository = ContactRepository(ContactDataBase.getInstance(context).contactDao())
        }
        return conRepository
    }

}