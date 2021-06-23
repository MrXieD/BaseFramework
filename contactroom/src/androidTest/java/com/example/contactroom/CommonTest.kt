package com.example.contactroom

import android.util.Log
import com.example.contactroom.util.DateUtil
import org.junit.Test

/**
@author Anthony.H
@date: 2021/6/23
@desription:
 */

class CommonTest {


    @Test
    fun tesTimeStamp2FormatDate() {
        val dateStr = DateUtil.timeStamp2FormatDate(1624413451858)
        Log.e("tesTimeStamp2FormatDate", "dateStr:$dateStr,${System.currentTimeMillis()}")
    }


}