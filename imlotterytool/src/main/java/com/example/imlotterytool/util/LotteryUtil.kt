package com.example.imlotterytool.util

import com.example.imlotterytool.LotteryType
import com.example.imlotterytool.db.table.*
import java.text.SimpleDateFormat
import java.util.*
/**
 *将福彩3d数据转换成数据库存储格式
 *
 */
fun convert2FcsdDBData(dataList: List<LotteryEntity>?): List<LotteryItem>? {
    dataList?.let {
        val fcsddbList = ArrayList<LotteryItem>()
        for (lotterData in it) {

            val oneList = ArrayList<OneLotteryNum>()
            val numberArray = convert2Numbers(lotterData.lotteryRes)
            //百位
            for (ballIndex in 0..9) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                if (ballIndex == numberArray[0]) {//选中了这个号
                    type = NORMAL_TYPE
                    numberShow = numberArray[0].toString()
                } else {//该号码未选中，不管遗漏值
                }
                oneList.add(OneLotteryNum(numberShow, type))
            }

            //十位
            for (ballIndex in 0..9) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                if (ballIndex == numberArray[1]) {//选中了这个号
                    type = NORMAL_TYPE
                    numberShow = numberArray[1].toString()
                } else {//该号码未选中，不管遗漏值
                }
                oneList.add(OneLotteryNum(numberShow, type))
            }

            //个位
            for (ballIndex in 0..9) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                if (ballIndex == numberArray[2]) {//选中了这个号
                    type = NORMAL_TYPE
                    numberShow = numberArray[2].toString()
                } else {//该号码未选中，不管遗漏值
                }
                oneList.add(OneLotteryNum(numberShow, type))
            }

            fcsddbList.add(LotteryItem(lotterData.lotteryNo, oneList))
        }
        return fcsddbList
    }
    return null
}

fun convert2Numbers(lotteryRes: String): Array<Int> {

    val strArray = lotteryRes.split(",")

    val numberArray = Array(strArray.size) { it }

    for (index in numberArray.indices) {
        numberArray[index] = strArray[index].toInt()
    }
    return numberArray
}


/**
 *
 * 获取最近一次3d开奖的时间，3d开奖是每天21:15分以后，一般延迟半个小时才能获取数据
 */

fun getLatestFcsdDate(): String {
    val df = SimpleDateFormat("yyyy-MM-dd")
    val nowClendar = Calendar.getInstance()//当前时间
    val openCalendar = Calendar.getInstance()//开奖时间
    openCalendar.let {
        it.set(Calendar.HOUR_OF_DAY, 21)
        it.set(Calendar.MINUTE, 30)
    }
    if (nowClendar.after(openCalendar) || nowClendar.equals(openCalendar)) {
        return df.format(nowClendar.time)
    }
    nowClendar.add(Calendar.DAY_OF_MONTH, -1)
    return df.format(nowClendar.time)
}

/**
 * 根据所求日期和每页50期，算出需要请求第几页
 */
fun calRequestPage(date: String): String {

    val latestDate = getLatestFcsdDate()
    val deltaDay = daysBetween(date, latestDate)
    return (deltaDay / 50 + 1).toString()
}

fun daysBetween(beforeDate: String, afterDate: String): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    val cal = Calendar.getInstance()
    cal.time = sdf.parse(beforeDate)
    val timeBefore = cal.timeInMillis
    cal.time = sdf.parse(afterDate)
    val timeAfter = cal.timeInMillis
    return (((timeAfter - timeBefore) / (1000 * 3600 * 24)).toInt())
}


fun getTitleListByLotteryType(lotteryType: LotteryType): List<String> {
    val arrayList = ArrayList<String>()

    when (lotteryType) {
        LotteryType.FCSD -> {
            for (outterIndex in 0..2) {
                for (innerIndex in 0..9) {
                    arrayList.add(innerIndex.toString())
                }
            }

        }

        LotteryType.SSQ -> {

        }
    }
    return arrayList
}