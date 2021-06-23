package com.example.contactroom.util

import java.text.SimpleDateFormat
import java.util.*

/**
@author Anthony.H
@date: 2021/6/23
@desription:
 */
object DateUtil {

    /**
     *
     * 根据时间戳转换成年月日，
     * 如果为当前年份，则值显示月和日
     */
    fun timeStamp2FormatDate(timeStamp: Long): String {
        val calendarTmp = Calendar.getInstance()
        val nowYear = calendarTmp.get(Calendar.YEAR)
        val targetDate = Date(timeStamp)
        calendarTmp.time = targetDate
        val targetYear = calendarTmp.get(Calendar.YEAR)
        var formatString = if (nowYear == targetYear) {
            "MM/dd"
        } else {
            "yyyyMM-dd"
        }
        val sdf = SimpleDateFormat(formatString)
        return sdf.format(targetDate)
    }

}