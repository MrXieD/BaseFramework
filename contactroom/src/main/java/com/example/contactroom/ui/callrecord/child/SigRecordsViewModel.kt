package com.example.contactroom.ui.callrecord.child

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.contactroom.data.IContactRepository
import com.example.contactroom.data.database.entity.CallRecord

/**
@author Anthony.H
@date: 2021/6/23
@desription:
 */
class SigRecordsViewModel(private val contactRepository: IContactRepository) : ViewModel() {


    lateinit var sigNameRecordsLiveData: LiveData<List<CallRecord>>

    lateinit var sigNumberRecodsLiveData: LiveData<List<CallRecord>>

    fun getSigRecordsByName(name: String) {
        sigNameRecordsLiveData = contactRepository.getSigCallRecordsByName(name).asLiveData()
    }

    fun getSigRecodsByNumber(number: String) {
        sigNumberRecodsLiveData = contactRepository.getSigCallRecordsByNumber(number).asLiveData()
    }


}