package com.example.contactroom.ui.call.child.allrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.contactroom.data.IContactRepository

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class AllRecordsViewModel(private val contactRepository: IContactRepository) : ViewModel() {


    val allCallRecordsLiveData = contactRepository.getAllCallRecords().asLiveData()

}