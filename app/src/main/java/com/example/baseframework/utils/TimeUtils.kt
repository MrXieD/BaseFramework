package com.example.baseframework.utils

import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {
    const val SECOND = 1000L
    const val MINUTE = 60 * SECOND
    const val HOUR = 60 * MINUTE

    const val DAY = 24 * HOUR

    const val YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss"
    const val YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS"
    const val YYYY_MM_DD = "yyyy-MM-dd"
    const val HH_MM_SS = "HH:mm:ss"
    const val HH_MM = "HH:mm"

    fun format(date: Long,
               pattern: String = YYYY_MM_DD_HH_MM_SS,
               locale: Locale = Locale.getDefault(),
               timeZone: TimeZone = TimeZone.getTimeZone("UTC")): String {
        return format(Date(date), pattern, locale, timeZone)
    }

    fun format(date: Date,
               pattern: String = YYYY_MM_DD_HH_MM_SS,
               locale: Locale = Locale.getDefault(),
               timeZone: TimeZone = TimeZone.getTimeZone("UTC")): String {
        val format = SimpleDateFormat(pattern, locale)
        format.timeZone = timeZone
        return format.format(date)
    }

    /**
     * 将指定时间格式化为 hh:mm:ss.sss的样式
     *
     * @param standard 如果为true则为标准模式，否则为精简模式，比如没有小时时将返回mm:ss.sss
     */
    fun format(time: Long, standard: Boolean): String {
        val ss = time % 1000
        val s = time / 1000 % 60
        val m = time / 1000 / 60 % 60
        val h = time / 1000 / 60 / 60
        if (standard || h > 0) {
            return String.format("%02d:%02d:%02d.%03d", h, m, s, ss)
        }

        if (m > 0) {
            return String.format("%02d:%02d.%03d", m, s, ss)
        }

        return String.format("%02d.%03d", m, s, ss)
    }

    fun parse(date: String,
              pattern: String,
              locale: Locale = Locale.getDefault(),
              timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Long {
        val parser = SimpleDateFormat(pattern, locale)
        parser.timeZone = timeZone
        return parser.runCatching {
            parse(date)?.time ?: -1L
        }.getOrDefault(-1)
    }

    fun now(): Long = System.currentTimeMillis()

    fun nowNano(): Long = System.nanoTime()

    /**
     * 计算当前时间距离指定时间过去了多久
     *
     * @param time 当前时间之前的一个时间
     */
    fun pastTime(time: Long): Long = System.currentTimeMillis() - time

}