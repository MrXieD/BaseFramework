package com.example.contactroom.util

import android.content.Context
import com.example.contactroom.data.ContactRepostoryFactory
import com.example.contactroom.data.IContactRepository
import com.example.contactroom.ui.call.child.allrecords.AllRecordsViewModelFactory
import com.example.contactroom.ui.call.child.allrecords.SigRecordsViewModelFactory

/**
@author Anthony.H
@date: 2021/6/10
@desription:
 */
object InjectUtil {


    private fun getContactRepostory(context: Context): IContactRepository =
        ContactRepostoryFactory.getReposiroty(context)

    fun getAllRecordsViewModelFactory(context: Context) = AllRecordsViewModelFactory(getContactRepostory(context))

    fun getSigRecordsViewModelFactory(context: Context) = SigRecordsViewModelFactory(getContactRepostory(context))
}