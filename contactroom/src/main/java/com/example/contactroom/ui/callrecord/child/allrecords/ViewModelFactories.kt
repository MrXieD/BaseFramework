package com.example.contactroom.ui.callrecord.child.allrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactroom.data.IContactRepository
import com.example.contactroom.ui.callrecord.child.SigRecordsViewModel
import com.example.contactroom.ui.callrecord.detail.contacts.ContactDetailViewModel

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
class AllRecordsViewModelFactory(private val contactRepository: IContactRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AllRecordsViewModel(contactRepository) as T
    }
}

class SigRecordsViewModelFactory(private val contactRepository: IContactRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SigRecordsViewModel(contactRepository) as T
    }
}

class ContactDetailViewModelFactory(private val contactRepository: IContactRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ContactDetailViewModel(contactRepository) as T
    }
}