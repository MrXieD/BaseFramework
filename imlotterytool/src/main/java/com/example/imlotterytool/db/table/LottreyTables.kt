package com.example.imlotterytool.db.table

import androidx.room.*
import com.google.gson.annotations.SerializedName


/**
@author Anthony.H
@date: 2021/5/13
@desription:
 */
/**
 *使用嵌套字段[Embedded]的方式，其他表尝试用[TypeConverter]
 * 3d数据表
 *
 */
//@Entity
//data class FCSDDBEntity(
//
//    @PrimaryKey @ColumnInfo(name = "lottery_date") val lotteryDate: String,//用日期来标记唯一
//
//    @Embedded val zero: OneLotteryNum,
//
//    @Embedded val two: OneLotteryNum,
//
//    @Embedded val three: OneLotteryNum,
//
//    @Embedded val four: OneLotteryNum,
//
//    @Embedded val five: OneLotteryNum,
//
//    @Embedded val six: OneLotteryNum,
//
//    @Embedded val seven: OneLotteryNum,
//
//    @Embedded val eight: OneLotteryNum,
//
//    @Embedded val nine: OneLotteryNum,
//
//    )
//
//


data class LotteryItem(val issues: String, val numbers: List<OneLotteryNum>)

/**[ballType]代表球的种类0:代表遗漏  1：红球 2:蓝球 3:无颜色球（比如3d这种，没有颜色）

[num]如果[ballType]>0则表示球号，否则表示该号遗漏的值
 */
data class OneLotteryNum(val num: String, val ballType: Int = MISS_TYPE)

//////////////////////////

@Entity
data class LotteryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @SerializedName("lottery_date") @ColumnInfo(name = "lottery_date") val lotteryDate: String,
    @SerializedName("lottery_id") @ColumnInfo(name = "lottery_id") val lotteryId: String,
    @SerializedName("lottery_res") @ColumnInfo(name = "lottery_res") val lotteryRes: String,
    @SerializedName("lottery_no") @ColumnInfo(name = "lottery_no") val lotteryNo: String
)

const val MISS_TYPE = 0x0

const val RED_BALL_TYPE = 0x1

const val BLUE_BALL_TYPE = 0x2

const val NORMAL_TYPE = 0x3

