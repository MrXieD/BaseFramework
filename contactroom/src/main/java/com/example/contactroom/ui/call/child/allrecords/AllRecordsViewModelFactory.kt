package com.example.contactroom.ui.call.child.allrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.contactroom.data.IContactRepository

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