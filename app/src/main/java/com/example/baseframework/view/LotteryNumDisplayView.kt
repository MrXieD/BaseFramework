package com.example.baseframework.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.baseframework.R
import com.example.baseframework.ex.*
import com.example.baseframework.log.XLog
import kotlin.math.abs
import kotlin.math.min
import kotlin.random.Random

/**
 * 彩票中奖号码展示View https://datachart.500.com/dlt/
 * 要求：
 * 1、可在XML中配置显示的可显示范围行数和列数以及总的行数和列数
 * 2、可水平和竖直惯性滑动及拖动，并且内容也做相应变化
 * 3、中奖号码内容配置
 * 4、中奖号码连线
 * 5、需要显示期号
 * 6、双指缩小和放大、双击放大或缩小
 *
 */
class LotteryNumDisplayView : View {
    //显示界面行数
    private var displayLineNum = 15

    //显示界面列数
    private var displayRowNum = 10

    //期号宽度
    private var dateWidth = context.dip2px(80)

    //数字方框宽度
    private var numWidth: Float = context.dip2px(16f)

    //数字方框宽度
    private var numHeight: Float = context.dip2px(16f)

    //字体大小
    private var numTextSize = 16f;

    //起始行Index
    private var offLineIndex = 0;

    //起始列Index
    private var offRowIndex = 0;

    //数据List
    private val dataList = mutableListOf<LotteryNumData>()

    //总列数
    private var totalRows = 0

    //总行数
    private var totalLines = 0

    //数字画笔
    private val mNumTextPaint: Paint = Paint()

    //网格画笔
    private val mMeshPaint: Paint = Paint()

    private val tempRect = Rect()

    private var mLastX = 0
    private var totalOffX = 0
    private var offMeshX = 0f


    private var mLastY = 0
    private var totalOffY = 0
    private var offMeshY = 0f


    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initTypedArray(attrs)
    }


    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    ) {
        initTypedArray(attrs)
    }

    init {
        mMeshPaint.isAntiAlias = true
        mMeshPaint.color = context.getColorResource(R.color.black)
        mMeshPaint.style = Paint.Style.STROKE
        mMeshPaint.strokeWidth = 1f

        mNumTextPaint.isAntiAlias = true
        mNumTextPaint.color = context.getColorResource(R.color.black)
        mNumTextPaint.textSize = context.sp2px(numTextSize)
        mNumTextPaint.textAlign = Paint.Align.CENTER
        for (i in 0 until 50) {
            dataList.add(randomNum(i))
        }
        totalLines = dataList.size
        totalRows = dataList[0].run {
            lotteryNumFrontList.size + lotteryNumBackList.size
        }

    }

    private fun randomNum(i: Int): LotteryNumData {
        val lotteryNumFrontList = ArrayList<LotteryNum>(5)
        val lotteryNumBackList = ArrayList<LotteryNum>(2)
        val numList = mutableListOf<Int>()
        //大乐透前五位 1-35
        for (k in 1..5) {
            var num = Random.nextInt(1, 36)
            while (numList.contains(num)) {
                num = Random.nextInt(1, 36)
            }
            numList.add(num)
        }
        numList.sort()
        for (k in 1..35) {
            lotteryNumFrontList.add(LotteryNum(k.toString(), numList.contains(k)))
        }
        numList.clear()
        //区号 1-12
        for (k in 1..2) {
            var num = Random.nextInt(1, 13)
            while (numList.contains(num)) {
                num = Random.nextInt(1, 13)
            }
            numList.add(num)
        }
        numList.sort()
        for (k in 1..12) {
            lotteryNumBackList.add(LotteryNum(k.toString(), numList.contains(k)))
        }
        return LotteryNumData(i.toString(), lotteryNumFrontList, lotteryNumBackList)
    }

    private fun initTypedArray(attrs: AttributeSet?) {


    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        numWidth = width / displayRowNum.toFloat()
        numHeight = height / displayLineNum.toFloat()
    }

    @SuppressLint("LongLogTag")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                val deltaX: Int = x - mLastX
                val deltaY: Int = y - mLastY
                //X轴最小和最大限制
                totalOffX += deltaX
                if (totalOffX >= 0 || totalRows * numWidth < width) {
                    totalOffX = 0
                } else if (abs(totalOffX) + width >= totalRows * numWidth) {
                    totalOffX = (-(totalRows * numWidth - width)).toInt()
                }
                offMeshX = totalOffX % numWidth
                offRowIndex = abs(totalOffX / numWidth).toInt()
                //Y轴最小和最大限制
                totalOffY += deltaY
                if (totalOffY >= 0) {
                    totalOffY = 0
                }
                if (totalLines * numHeight < height) {
                    totalOffY = 0
                } else {
                    if (abs(totalOffY) + height >= totalLines * numHeight) {
                        totalOffY = (-(totalLines * numHeight - height)).toInt()
                    }
                }
                offMeshY = totalOffY % numHeight
                offLineIndex = abs(totalOffY / numHeight).toInt()
                XLog.i("offRowIndex--->$offRowIndex")
                XLog.i("offLineIndex--->$offLineIndex")
                invalidate()
            }
        }
        mLastX = x
        mLastY = y
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startLineIndex = offLineIndex
        val startRowIndex = offRowIndex
        //首先绘制网格
        drawMesh(canvas)
        drawNum(canvas, startLineIndex, startRowIndex)
    }

    private fun drawNum(canvas: Canvas, startLineIndex: Int, startRowIndex: Int) {
        canvas.saveAndRestore {
            canvas.translate(offMeshX, offMeshY)
            for (lineIndex in startLineIndex..startLineIndex + displayLineNum) {
                if (lineIndex < dataList.size) {
                    val numText = dataList[lineIndex]
                    //绘制一行中的数字
                    canvas.saveAndRestore {
                        //绘制前五位
                        for (numIndex in startRowIndex..if (startRowIndex + displayRowNum < numText.lotteryNumFrontList.size) startRowIndex + displayRowNum else numText.lotteryNumFrontList.size - 1) {
                            val num = numText.lotteryNumFrontList[numIndex]
                            mNumTextPaint.getTextBounds(num.num, 0, num.num.length, tempRect)
                            num.isLottery.doTrue {
                                    mNumTextPaint.color = context.getColorResource(R.color.colorAccent)
                                    canvas.drawCircle(numWidth / 2, numHeight / 2, min(numWidth / 2f * 0.8f, numHeight / 2f * 0.8f), mNumTextPaint)
                            }
                            mNumTextPaint.color = context.getColorResource(if (num.isLottery) R.color.white else R.color.black)
                            canvas.drawText(num.num, (numWidth) / 2, (numHeight + tempRect.height()) / 2, mNumTextPaint)
                            canvas.translate(numWidth, 0f)
                        }
                        //绘制后两位
                        if (startRowIndex + displayRowNum >= numText.lotteryNumFrontList.size) {
                            val startIndex = if (startRowIndex < numText.lotteryNumFrontList.size) 0 else startRowIndex - numText.lotteryNumFrontList.size
                            val endIndex = startRowIndex + displayRowNum - numText.lotteryNumFrontList.size
                            for (numIndex in startIndex..endIndex) {
                                if (numIndex < numText.lotteryNumBackList.size) {
                                    val num = numText.lotteryNumBackList[numIndex]
                                    num.isLottery.doTrue {
                                            mNumTextPaint.color = context.getColorResource(R.color.colorAccent)
                                            canvas.drawCircle(numWidth / 2, numHeight / 2, min(numWidth / 2f * 0.8f, numHeight / 2f * 0.8f), mNumTextPaint)
                                    }
                                    mNumTextPaint.color = context.getColorResource(if (num.isLottery) R.color.white else R.color.black)
                                    mNumTextPaint.getTextBounds(num.num, 0, num.num.length, tempRect)
                                    canvas.drawText(num.num, (numWidth) / 2, (numHeight + tempRect.height()) / 2, mNumTextPaint)
                                    canvas.translate(numWidth, 0f)
                                }
                            }
                        }
                    }
                    canvas.translate(0f, numHeight)
                }
            }
        }
    }

    private fun drawMesh(canvas: Canvas) {
        canvas.saveAndRestore {
            canvas.translate(offMeshX, 0f)
            canvas.saveAndRestore {
                for (i in 0..displayRowNum) {
                    canvas.drawLine(0f, 0f, 0f, height.toFloat(), mMeshPaint)
                    canvas.translate(numWidth, 0f)
                }
            }
        }
        canvas.saveAndRestore {
            canvas.translate(0f, offMeshY)
            canvas.saveAndRestore {
                for (i in 0..displayLineNum) {
                    canvas.drawLine(0f, 0f, width.toFloat(), 0f, mMeshPaint)
                    canvas.translate(0f, numHeight)
                }
            }
        }
    }

    data class LotteryNumData(val date: String, val lotteryNumFrontList: MutableList<LotteryNum>, val lotteryNumBackList: MutableList<LotteryNum>)
    data class LotteryNum(val num: String, val isLottery: Boolean)
}