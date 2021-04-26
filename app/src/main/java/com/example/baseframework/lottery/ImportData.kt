package com.example.baseframework.lottery

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.baseframework.view.LotteryNumDisplayView
import kotlinx.coroutines.*
import okhttp3.internal.wait
import org.apache.poi.hssf.usermodel.HSSFDateUtil
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.FormulaEvaluator
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import kotlin.coroutines.CoroutineContext


/**
created by AnthonyH
createDate: 2020/9/29 0029
彩票excel数据读取测试工具类
 */
object ImportData {

    private const val TAG = "ImportData"
    private val formatter =
        SimpleDateFormat("yyyy/MM/dd")
    private val mainHandler = Handler(Looper.getMainLooper())
    private var tmpRedNumberList = ArrayList<Int>()
    private var tmpBlueNumberList = ArrayList<Int>()
    private var job:Job?=null
    fun run(context: Context, importListener: ImportDataListener) {
        context!!.run {
            try {
                job?.run {
                    if (isActive) {
                        cancel()
                    }
                }
                job = GlobalScope.launch(context = Dispatchers.Default) {
                    doImport(context, importListener)
                }
            } catch (e: java.lang.Exception) {

                callBackError(e, importListener)
            }
        }
    }

    private fun callBackSuc(
        lotteryList: ArrayList<LotteryNumDisplayView.OneDateLotteryData>,
        listener: ImportDataListener
    ) {
        listener?.run { mainHandler.post { onSuccced(lotteryList) } }
    }

    private fun callBackError(e: java.lang.Exception, listener: ImportDataListener) {
        listener?.run { mainHandler.post { onError(e) } }
    }

    private fun callBackProgress(listener: ImportDataListener, index: Long) {
        listener?.run { mainHandler.post { onProgress(index) } }
    }


    private fun doImport(context: Context, importListener: ImportDataListener) {
        val inputStream = context.resources.assets.open("lottery_history.xls")
        if (inputStream != null) {
            val lotteryList = ArrayList<LotteryNumDisplayView.OneDateLotteryData>()
            val workbook = HSSFWorkbook(inputStream)
            val sheet = workbook.getSheetAt(0)
            val rowCount = sheet.physicalNumberOfRows
            for (rowIndex in 0 until rowCount) {
                val row: Row = sheet.getRow(rowIndex)
                val rowCellCount = row.physicalNumberOfCells
                val formulaEvaluator: FormulaEvaluator =
                    workbook.creationHelper.createFormulaEvaluator()
                if (rowIndex == 1839) {
                    Log.e(TAG, "doImport: ...")
                }
                if (rowCellCount == 9) {
                    try {
                        val cellDate = row.getCell(6)
                        if (HSSFDateUtil.isCellDateFormatted(cellDate)) {
                            val dateString = formatter.format(
                                HSSFDateUtil.getJavaDate(
                                    formulaEvaluator.evaluate(cellDate).numberValue
                                )
                            )
                            val lssueNumber =
                                formulaEvaluator.evaluate(row.getCell(0)).numberValue.toInt()

                            tmpRedNumberList?.apply {
                                clear()
                                add(formulaEvaluator.evaluate(row.getCell(1)).numberValue.toInt())
                                add(formulaEvaluator.evaluate(row.getCell(2)).numberValue.toInt())
                                add(formulaEvaluator.evaluate(row.getCell(3)).numberValue.toInt())
                                add(formulaEvaluator.evaluate(row.getCell(4)).numberValue.toInt())
                                add(formulaEvaluator.evaluate(row.getCell(5)).numberValue.toInt())
                            }
                            tmpBlueNumberList?.apply {
                                clear()
                                add(formulaEvaluator.evaluate(row.getCell(7)).numberValue.toInt())
                                add(formulaEvaluator.evaluate(row.getCell(8)).numberValue.toInt())
                            }
                            val rowData =
                                createItemData(
                                    tmpRedNumberList,
                                    tmpBlueNumberList,
                                    lssueNumber,
                                    lotteryList
                                )
                            lotteryList.add(rowData)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e(TAG, "跳过一次:${e.message} ")
                    }
                }
                Log.e(TAG, "doImport: $rowIndex")
                callBackProgress(importListener, rowIndex.toLong())
            }
            callBackSuc(lotteryList, importListener)
        }
    }

    private fun createItemData(
        tmpRedNumberList: ArrayList<Int>,
        tmpBlueNumberList: ArrayList<Int>,
        lssueNumber: Int,
        lotteryList: ArrayList<LotteryNumDisplayView.OneDateLotteryData>
    ): LotteryNumDisplayView.OneDateLotteryData {
        val list = ArrayList<LotteryNumDisplayView.OneLotteryNum>()
        for (redBall in 1..35) {
            if (tmpRedNumberList.contains(redBall)) {
                list.add(LotteryNumDisplayView.OneLotteryNum(redBall.toString(), true, 1))
            } else {
                var missIndex = 1
                if (lotteryList.isNotEmpty()) {
                    val lastLottery = lotteryList.last()
                    val lastRow = lastLottery.numList
                    if (!lastRow[redBall - 1].isLottery) {
                        missIndex = lastRow[redBall - 1].num.toInt() + 1
                    }
                }
                list.add(LotteryNumDisplayView.OneLotteryNum(missIndex.toString(), false, -1))
            }
        }

        for (blueBall in 1..12) {
            if (tmpBlueNumberList.contains(blueBall)) {
                list.add(LotteryNumDisplayView.OneLotteryNum(blueBall.toString(), true, 2))
            } else {
                var missIndex = 1
                if (lotteryList.isNotEmpty()) {
                    val lastLottery = lotteryList.last()
                    val lastRow = lastLottery.numList
                    if (!lastRow[34 + blueBall].isLottery) {
                        missIndex = lastRow[34 + blueBall].num.toInt() + 1
                    }
                }
                list.add(LotteryNumDisplayView.OneLotteryNum(missIndex.toString(), false, -1))
            }
        }

        return LotteryNumDisplayView.OneDateLotteryData(lssueNumber.toString(), list)
    }


    interface ImportDataListener {
        fun onProgress(index: Long)
        fun onSuccced(list: ArrayList<LotteryNumDisplayView.OneDateLotteryData>)
        fun onError(e: Exception)
    }

}