package com.example.imlotterytool

import android.util.Log
import com.example.imlotterytool.util.calRequestPage
import com.example.imlotterytool.util.getLatestFcsdDate
import org.junit.Test

/**
@author Anthony.H
@date: 2021/5/14
@desription:
 */
class LotteryImTest {


    @Test
    fun testgetLatestFcsdDate() {

        Log.e("testgetLatestFcsdDate", getLatestFcsdDate())
    }

    @Test
    fun testcalRequestPage() {

        Log.e("testcalRequestPage", "testcalRequestPage: ${calRequestPage("2021-2-10")}")
    }
}