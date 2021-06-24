package com.example.contactroom.ui.callrecord.detail.contacts

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.contactroom.data.IContactRepository

/**
@author Anthony.H
@date: 2021/6/24
@desription:
 */
class ContactDetailViewModel(private val contactRepository: IContactRepository) : ViewModel() {


    lateinit var contactDetailLiveData: LiveData<List<String>>

    fun getNumbersForContact(name: String) {
        contactDetailLiveData = contactRepository.getNumbersForContact(name).asLiveData()
    }

}