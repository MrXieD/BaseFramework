package com.example.imlotterytool.util

import com.example.imlotterytool.db.table.*
import java.lang.NullPointerException
import java.text.SimpleDateFormat
import java.util.*


fun convertDb2Result(
    lotteryId: String,
    dataList: List<LotteryEntity>?,
    isShowMiss: Boolean = true
): MutableList<LotteryItem>? {
    dataList?.let {
        when (lotteryId) {
            LOTTERY_TYPE_FCSD,LOTTERY_TYPE_PL3 -> {
                return convert2_FCSD_BData(it,3)
            }
            LOTTERY_TYPE_PL5 -> {
                return convert2_FCSD_BData(it,5)
            }
            LOTTERY_TYPE_7XC -> {
                return convert2_7XC_BData(it)
            }
            LOTTERY_TYPE_SSQ -> {
                return convert2SsqDBData(it)
            }
            LOTTERY_TYPE_DLT -> {
                return convert2CjdltDBData(it)
            }
            else -> {
                throw NullPointerException("未指定的彩票类型")
            }
        }
    }
    return null
}

fun convert2CjdltDBData(dataList: List<LotteryEntity>): MutableList<LotteryItem>? {
    dataList.let {
        val allList = ArrayList<LotteryItem>()
        for (lotterData in it) {
            val oneList = ArrayList<OneLotteryNum>()
            val numberArray = convert2Numbers(lotterData.lotteryRes)
            //红球
            for (ballIndex in 1..35) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                val selectedNumbder = checkCjdltRedIsSelect(ballIndex, numberArray)
                numberShow = if (selectedNumbder > 0) {
                    type = RED_BALL_TYPE
                    selectedNumbder.toString()
                } else numberShow
                oneList.add(OneLotteryNum(numberShow, type))
            }
            //蓝球
            for (ballIndex in 1..12) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                val selectedNumbder = checkCjdltBlueIsSelect(ballIndex, numberArray)
                numberShow = if (selectedNumbder > 0) {
                    type = BLUE_BALL_TYPE
                    selectedNumbder.toString()
                } else numberShow
                oneList.add(OneLotteryNum(numberShow, type))
            }
            allList.add(LotteryItem(lotterData.lotteryNo, oneList))
        }
        return allList
    }
    return null
}

fun checkCjdltBlueIsSelect(ballIndex: Int, numberArray: List<Int>): Int {
    numberArray.run {
        for (index in 5 until numberArray.size) {
            if (numberArray[index] == ballIndex) {
                return numberArray[index]
            }
        }
    }
    return -1
}

fun checkCjdltRedIsSelect(ballIndex: Int, numberArray: List<Int>): Int {
    numberArray.run {
        for (index in 0..4) {
            if (numberArray[index] == ballIndex) {
                return numberArray[index]
            }
        }
    }
    return -1
}

fun convert2SsqDBData(dataList: List<LotteryEntity>): MutableList<LotteryItem>? {

    dataList?.let {
        val fcsddbList = ArrayList<LotteryItem>()
        for (lotterData in it) {
            val oneList = ArrayList<OneLotteryNum>()
            val numberArray = convert2Numbers(lotterData.lotteryRes)
            //红球
            for (ballIndex in 1..33) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                val selectedNumbder = checkSsqRedIsSelect(ballIndex, numberArray)
                numberShow = if (selectedNumbder > 0) {
                    type = RED_BALL_TYPE
                    selectedNumbder.toString()
                } else numberShow
                oneList.add(OneLotteryNum(numberShow, type))
            }
            //蓝球
            for (ballIndex in 1..16) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                val selectedNumbder = checkSsqBlueIsSelect(ballIndex, numberArray)
                numberShow = if (selectedNumbder > 0) {
                    type = BLUE_BALL_TYPE
                    selectedNumbder.toString()
                } else numberShow
                oneList.add(OneLotteryNum(numberShow, type))
            }
            fcsddbList.add(LotteryItem(lotterData.lotteryNo, oneList))
        }
        return fcsddbList
    }
    return null
}

fun checkSsqBlueIsSelect(ballIndex: Int, numberArray: List<Int>): Int {
    numberArray?.run {
        if (numberArray[6] == ballIndex) {
            return numberArray[6]
        }
    }
    return -1
}


fun checkSsqRedIsSelect(ballIndex: Int, numberArray: List<Int>): Int {
    numberArray.run {
        for (index in 0..5) {
            if (numberArray[index] == ballIndex) {
                return numberArray[index]
            }
        }
    }
    return -1
}


/**
 *将福彩3d数据转换成数据库存储格式
 *
 */
fun convert2_FCSD_BData(dataList: List<LotteryEntity>?,bitCount:Int, isShowMiss: Boolean = true):
        MutableList<LotteryItem>? {
    dataList?.let {
        val lotteryList = ArrayList<LotteryItem>()
        for (i in it.indices) {
            val oneList = ArrayList<OneLotteryNum>()
            val numberArray = convert2Numbers(it[i].lotteryRes)
            for (bitIndex in 0 until bitCount){
                for (ballIndex in 0..9) {
                    var numberShow = "-"
                    var type: Int = MISS_TYPE
                    if (ballIndex == numberArray[bitIndex]) {//选中了这个号
                        type = NORMAL_TYPE
                        numberShow = numberArray[bitIndex].toString()
                    } else if (isShowMiss) {//该号码未选中，不管遗漏值
                        var missNum = "1"
                        if (i != 0) {
                            val lastNumData = lotteryList.last().numbers[ballIndex + 10 * bitIndex]
                            missNum = if (lastNumData.ballType == MISS_TYPE) (lastNumData.num.toInt() + 1).toString()
                            else "1"
                        }
                        numberShow = missNum
                    }
                    oneList.add(OneLotteryNum(numberShow, type))
                }
            }
            lotteryList.add(LotteryItem(it[i].lotteryNo, oneList))
        }
        return lotteryList
    }
    return null
}

fun convert2_7XC_BData(dataList: List<LotteryEntity>?, isShowMiss: Boolean = true): MutableList<LotteryItem>? {
    dataList?.let {
        val lotteryList = ArrayList<LotteryItem>()
        for (i in it.indices) {
            val oneList = ArrayList<OneLotteryNum>()
            val numberArray = convert2Numbers(it[i].lotteryRes)
            for (bitIndex in 0 until 6){
                for (ballIndex in 0..9) {
                    var numberShow = "-"
                    var type: Int = MISS_TYPE
                    if (ballIndex == numberArray[bitIndex]) {//选中了这个号
                        type = NORMAL_TYPE
                        numberShow = numberArray[bitIndex].toString()
                    } else if (isShowMiss) {//该号码未选中，不管遗漏值
                        var missNum = "1"
                        if (i != 0) {
                            val lastNumData = lotteryList.last().numbers[ballIndex + 10 * bitIndex]
                            missNum = if (lastNumData.ballType == MISS_TYPE) (lastNumData.num.toInt() + 1).toString()
                            else "1"
                        }
                        numberShow = missNum
                    }
                    oneList.add(OneLotteryNum(numberShow, type))
                }
            }
            for (ballIndex in 0..14) {
                var numberShow = "-"
                var type: Int = MISS_TYPE
                if (ballIndex == numberArray[6]) {//选中了这个号
                    type = NORMAL_TYPE
                    numberShow = numberArray[6].toString()
                } else if (isShowMiss) {//该号码未选中，不管遗漏值
                    var missNum = "1"
                    if (i != 0) {
                        val lastNumData = lotteryList.last().numbers[ballIndex + 10 * 6]
                        missNum = if (lastNumData.ballType == MISS_TYPE) (lastNumData.num.toInt() + 1).toString()
                        else "1"
                    }
                    numberShow = missNum
                }
                oneList.add(OneLotteryNum(numberShow, type))
            }

            lotteryList.add(LotteryItem(it[i].lotteryNo, oneList))
        }
        return lotteryList
    }
    return null
}

fun convert2Numbers(lotteryRes: String): List<Int> = lotteryRes.split(",").map { it.toInt() }


fun getLatestLotteryDateByType(lotteryId: String): String {
    return when (lotteryId) {
        LOTTERY_TYPE_FCSD,LOTTERY_TYPE_PL3,LOTTERY_TYPE_PL5 -> {
            getLatestFcsdDate()
        }
        LOTTERY_TYPE_SSQ -> {
            getLatestSsqDate()
        }
        LOTTERY_TYPE_DLT -> {
            getLatestCjdltDate()
        }
        LOTTERY_TYPE_7XC -> {
            getLatestQXCDate()
        }
        else -> throw NullPointerException("未指定的彩票类型")
    }
}

/**
 *
 * 获取最近一次3d开奖的时间，3d开奖是每天21:15分以后，一般延迟半个小时才能获取数据
 */

fun getLatestFcsdDate(): String {
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val nowClendar = Calendar.getInstance()//当前时间
    //开奖时间
    val openCalendar = Calendar.getInstance().run {
        set(Calendar.HOUR_OF_DAY, 21)
        set(Calendar.MINUTE, 30)
    }
    if (nowClendar.after(openCalendar) || nowClendar.equals(openCalendar)) {
        return df.format(nowClendar.time)
    }
    nowClendar.add(Calendar.DAY_OF_MONTH, -1)
    return df.format(nowClendar.time)
}

/**
 *
 * 获取最近一次双色球开奖的时间，开奖是每周二、四、七 21:15分以后，一般延迟十五分钟到半个小时才能获取数据
 */
fun getLatestSsqDate(): String {
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val nowClendar = Calendar.getInstance()
    var w = nowClendar[Calendar.DAY_OF_WEEK]
    val openCalendar = Calendar.getInstance()//依据当前时间来推算开奖时间
    when (w) {
        Calendar.TUESDAY, Calendar.THURSDAY, Calendar.SUNDAY -> {//二四七，当日开奖
            openCalendar.let {
                it.set(Calendar.HOUR_OF_DAY, 21)
                it.set(Calendar.MINUTE, 30)
            }
            if (nowClendar.after(openCalendar) || nowClendar.equals(openCalendar)) {
                return df.format(nowClendar.time)
            }
            var deltaDay = -2
            if (w == Calendar.SUNDAY) {
                deltaDay = -3
            }
            nowClendar.add(Calendar.DAY_OF_MONTH, deltaDay)
            return df.format(nowClendar.time)
        }
        Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY -> {//一三五，前一天开奖
            nowClendar.add(Calendar.DAY_OF_MONTH, -1)
            return df.format(nowClendar.time)
        }
        else -> {//六，倒推两天开奖
            nowClendar.add(Calendar.DAY_OF_MONTH, -2)
            return df.format(nowClendar.time)
        }
    }
}
/**
 *
 * 获取最近一次七星彩开奖的时间，开奖是每周二、五、七 21:15分以后，一般延迟十五分钟到半个小时才能获取数据
 */
fun getLatestQXCDate(): String {
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val nowClendar = Calendar.getInstance()
    var w = nowClendar[Calendar.DAY_OF_WEEK]
    val openCalendar = Calendar.getInstance()//依据当前时间来推算开奖时间
    when (w) {
        Calendar.TUESDAY, Calendar.FRIDAY, Calendar.SUNDAY -> {//二五七，当日开奖
            openCalendar.let {
                it.set(Calendar.HOUR_OF_DAY, 20)
                it.set(Calendar.MINUTE, 45)
            }
            if (nowClendar.after(openCalendar) || nowClendar.equals(openCalendar)) {
                return df.format(nowClendar.time)
            }
            var deltaDay = -2
            if (w == Calendar.FRIDAY) {
                deltaDay = -3
            }
            nowClendar.add(Calendar.DAY_OF_MONTH, deltaDay)
            return df.format(nowClendar.time)
        }
        Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.SATURDAY -> {//一三六，前一天开奖
            nowClendar.add(Calendar.DAY_OF_MONTH, -1)
            return df.format(nowClendar.time)
        }
        else -> {//六，倒推两天开奖
            nowClendar.add(Calendar.DAY_OF_MONTH, -2)
            return df.format(nowClendar.time)
        }
    }
}

/**
 *
 * 获取最近一次超级大乐透开奖的时间，开奖是每周一、三、六 20:30分以后，一般延迟十五分钟到半个小时才能获取数据
 */
fun getLatestCjdltDate(): String {
    val df = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val nowClendar = Calendar.getInstance()
    var w = nowClendar[Calendar.DAY_OF_WEEK]
    val openCalendar = Calendar.getInstance()//依据当前时间来推算开奖时间
    when (w) {
        Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.SATURDAY -> {//一三六，当日开奖
            openCalendar.let {
                it.set(Calendar.HOUR_OF_DAY, 20)
                it.set(Calendar.MINUTE, 45)
            }
            if (nowClendar.after(openCalendar) || nowClendar.equals(openCalendar)) {
                return df.format(nowClendar.time)
            }
            var deltaDay = -2
            if (w == Calendar.SATURDAY) {
                deltaDay = -3
            }
            nowClendar.add(Calendar.DAY_OF_MONTH, deltaDay)
            return df.format(nowClendar.time)
        }
        Calendar.TUESDAY, Calendar.THURSDAY, Calendar.SUNDAY -> {//二四七，前一天开奖
            nowClendar.add(Calendar.DAY_OF_MONTH, -1)
            return df.format(nowClendar.time)
        }
        else -> {//五，倒推两天开奖
            nowClendar.add(Calendar.DAY_OF_MONTH, -2)
            return df.format(nowClendar.time)
        }
    }
}


/**
 * 根据所求日期和每页50期，算出需要请求第几页
 */

fun calRequestPage(date: String): String {
    val latestDate = getLatestFcsdDate()
    val deltaDay = daysBetween(date, latestDate) + 1
    return (deltaDay / PAGE_SIZE + 1).toString()
}

fun daysBetween(beforeDate: String, afterDate: String): Int {
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
    val cal = Calendar.getInstance()
    cal.time = sdf.parse(beforeDate)
    val timeBefore = cal.timeInMillis
    cal.time = sdf.parse(afterDate)
    val timeAfter = cal.timeInMillis
    return (((timeAfter - timeBefore) / (1000 * 3600 * 24)).toInt())
}


fun getTitleListByLotteryType(lotteryId: String): List<String> {
    val arrayList = ArrayList<String>()
    when (lotteryId) {
        LOTTERY_TYPE_FCSD,LOTTERY_TYPE_PL3 -> {
            for (outterIndex in 0..2) {
                for (innerIndex in 0..9) {
                    arrayList.add(innerIndex.toString())
                }
            }
        }
        LOTTERY_TYPE_PL5 -> {
            for (outterIndex in 0..4) {
                for (innerIndex in 0..9) {
                    arrayList.add(innerIndex.toString())
                }
            }
        }
        LOTTERY_TYPE_7XC -> {
            for (outterIndex in 0..5) {
                for (innerIndex in 0..9) {
                    arrayList.add(innerIndex.toString())
                }
            }
            for (innerIndex in 0..14) {
                arrayList.add(innerIndex.toString())
            }
        }
        LOTTERY_TYPE_SSQ -> {
            for (redIndex in 1..33) {
                arrayList.add(redIndex.toString())
            }
            for (blueIndex in 1..16) {
                arrayList.add(blueIndex.toString())
            }
        }

        LOTTERY_TYPE_DLT -> {
            for (redIndex in 1..35) {
                arrayList.add(redIndex.toString())
            }
            for (blueIndex in 1..12) {
                arrayList.add(blueIndex.toString())
            }
        }
    }
    return arrayList
}