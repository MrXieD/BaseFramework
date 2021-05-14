package com.example.imlotterytool.util

import com.example.imlotterytool.db.table.*


/**
 *将福彩3d数据转换成数据库存储格式
 *
 */
fun convert2FcsdDBData(dataList: List<LotteryEntity>?, lastLotteryItem: LotteryEntity?): List<LotteryItem>? {
    dataList?.let {
        val fcsddbList = ArrayList<LotteryItem>()
        for (lotterData in it) {

            val oneList = ArrayList<OneLotteryNum>()
            val numberArray = convert2Numbers(lotterData.lotteryRes)
            //百位
            for (ballIndex in 0..9) {
                var numberShow: String = "null"
                var type: Int = MISS_TYPE
                if (ballIndex == numberArray[0]) {//选中了这个号
                    type = NORMAL_TYPE
                } else {//该号码未选中，计算遗漏值
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
