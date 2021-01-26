package com.example.baseframework.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.baseframework.R
import com.example.baseframework.ex.dip2px
import com.example.baseframework.ex.getColorResource
import com.example.baseframework.ex.saveAndRestore
import com.example.baseframework.ex.sp2px
import com.example.baseframework.log.XLog
import kotlin.random.Random

/**
 * 彩票中奖号码展示View //仿 https://datachart.500.com/dlt/
 * 要求：
 * 1、可在XML中配置显示的可显示范围行数和列数以及总的行数和列数
 * 2、可水平和竖直惯性滑动，并且内容也做相应变化
 * 3、中奖号码内容配置
 * 4、中奖号码连线
 */
class LotteryNumDisplayView : View {
    //显示界面行数
    private var displayLineNum = 10

    //显示界面列数
    private var displayRowNum = 10

    //期号宽度
    private var dateWidth = context.dip2px(80)

    //数字方框宽度
    private var numWidth: Float = context.dip2px(16f)

    //数字方框宽度
    private var numHeight: Float = context.dip2px(16f)

    //字体大小
    private var numTextSize = 16;

    //起始行Index
    private var offLineIndex = 0;

    //起始列Index
    private var offRowIndex = 0;

    private val dataList = mutableListOf<LotteryNum>()

    private val mPaint: Paint = Paint()


    private val mNumTextPaint: Paint = Paint()

    private val tempRect = Rect()


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
        mPaint.isAntiAlias = true
        mPaint.color = context.getColorResource(R.color.black)
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 1f

        mNumTextPaint.isAntiAlias = true
        mNumTextPaint.color = context.getColorResource(R.color.black)
        mNumTextPaint.textSize = context.sp2px(16f)
        for (i in 0 until 2) {
            dataList.add(randomNum(i))
        }
    }

    private fun randomNum(i: Int): LotteryNum {
        val lotteryNumFrontList = ArrayList<String>(5)
        val lotteryNumBackList = ArrayList<String>(2)
        val numList = mutableListOf<Int>()
        //大乐透前五位 1-35
        for (k in 1..5) {
            var num = Random.nextInt(1, 35)
            while (numList.contains(num)) {
                num = Random.nextInt(1, 35)
            }
            numList.add(num)
        }
        numList.sort()
        numList.forEach {
            lotteryNumFrontList.add(it.toString())
        }
        numList.clear()
        //区号 1-12
        for (k in 1..2) {
            var num = Random.nextInt(1, 12)
            while (numList.contains(num)) {
                num = Random.nextInt(1, 12)
            }
            numList.add(num)
        }
        numList.sort()
        numList.forEach {
            lotteryNumBackList.add(it.toString())
        }
        return LotteryNum(i.toString(), lotteryNumFrontList, lotteryNumBackList)
    }

    private fun initTypedArray(attrs: AttributeSet?) {


    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
            }
            MotionEvent.ACTION_MOVE -> {
            }
            MotionEvent.ACTION_UP -> {
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        numWidth = width / displayRowNum.toFloat()
        numHeight = height / displayLineNum.toFloat()

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startLineIndex = offLineIndex
        val startRowIndex = offRowIndex
        //首先绘制网格
        canvas.saveAndRestore {
            for (i in 0..displayLineNum + 1) {
                canvas.drawLine(0f, 0f, displayLineNum * numWidth, 0f, mPaint)
                canvas.translate(0f, numHeight)
            }
        }
        canvas.saveAndRestore {
            for (i in 0..displayRowNum + 1) {
                canvas.drawLine(0f, 0f, 0f, displayRowNum * numHeight, mPaint)
                canvas.translate(numWidth, 0f)
            }
        }
        //绘制数字
        //每一行
        canvas.saveAndRestore {
            for (index in startLineIndex until startLineIndex + displayLineNum) {
                if (index < dataList.size) {
                    val numText = dataList[index]
                    //绘制每个数字
                    canvas.saveAndRestore {
                    for (num in numText.lotteryNumFrontList) {
                            val textWidth = mPaint.getTextBounds(num, 0, num.length, tempRect)
                            XLog.i("lotteryNumFrontList----textWidth--->$textWidth")
                            canvas.drawText(num,(numWidth-tempRect.width())/2,(numHeight+tempRect.height())/2,mNumTextPaint)
                            canvas.translate(numWidth, 0f)
                        }
                        for (num in numText.lotteryNumBackList) {
                            val textWidth = mPaint.getTextBounds(num, 0, num.length, tempRect)
                            XLog.i("lotteryNumBackList---textWidth--->$textWidth")
                            canvas.drawText(num,(numWidth-tempRect.width())/2,(numHeight+tempRect.height())/2,mNumTextPaint)
                            canvas.translate(numWidth, 0f)
                        }

                    }
                    canvas.translate(0f, numHeight)
                }
            }
        }

    }

    data class LotteryNum(val date: String, val lotteryNumFrontList: MutableList<String>, val lotteryNumBackList: MutableList<String>)
}