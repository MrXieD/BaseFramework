package com.example.baseframework.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.baseframework.R
import com.example.baseframework.ex.*
import com.example.baseframework.log.XLog
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
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
    private var displayRowNum = 15

    //显示界面列数
    private var displayLineNum = 10

    //期号宽度
    private var dateWidth = context.dip2px(64f)

    //数字方框宽度
    private var numWidth: Float = context.dip2px(16f)

    //数字方框宽度
    private var numHeight: Float = context.dip2px(16f)

    //字体大小
    private var numTextSize = 16f

    //起始行Index
    private var offRowIndex = 0

    //起始列Index
    private var offLineIndex = 0

    //数据List
    private val dataList = mutableListOf<LotteryNumData>()

    //总列数
    private var totalLines = 0

    //总行数
    private var totalRows = 0

    //数字画笔
    private val mNumTextPaint: Paint = Paint()

    //网格画笔
    private val mMeshPaint: Paint = Paint()

    private val tempRect = Rect()

    private var mLastX = 0
    private var mLastY = 0

    //网格X轴偏移量
    private var totalScrollX = 0

    //网格Y轴偏移量
    private var meshScrollX = 0f

    private var totalScrollY = 0

    //网格Y轴偏移量
    private var meshScrollY = 0f

    private var flingGestureDetector: GestureDetector

    private var mExecutor = Executors.newSingleThreadScheduledExecutor()

    private var mFuture: ScheduledFuture<*>? = null

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
        totalRows = dataList.size
        totalLines = dataList[0].run {
            lotteryNumFrontList.size + lotteryNumBackList.size
        }

        //手势检测
        flingGestureDetector = GestureDetector(context, ScrollGestureListener())
        flingGestureDetector.setIsLongpressEnabled(false)
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
        numWidth = width / displayLineNum.toFloat()
        numHeight = height / displayRowNum.toFloat()
    }

    private fun checkScrollX() {
        if (totalScrollX >= 0 || totalLines * numWidth < width) {
            totalScrollX = 0
        } else if (abs(totalScrollX + dateWidth) + width >= totalLines * numWidth) {
            totalScrollX = (-(totalLines * numWidth + dateWidth - width)).toInt()
        }
    }

    private fun checkScrollY() {
        if (totalScrollY >= 0 || totalRows * numHeight < height) {
            totalScrollY = 0
        } else {
            if (abs(totalScrollY + numHeight) + height >= totalRows * numHeight) {
                totalScrollY = (-(totalRows * numHeight + numHeight - height)).toInt()
            }
        }
    }

    @SuppressLint("LongLogTag")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                //按下时停止滚动
                cancelFuture()
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX: Int = x - mLastX
                val deltaY: Int = y - mLastY
                //X轴最小和最大限制
                totalScrollX += deltaX
                checkScrollX()
                meshScrollX = totalScrollX % numWidth
                offLineIndex = abs(totalScrollX / numWidth).toInt()
                //Y轴最小和最大限制
                totalScrollY += deltaY
                checkScrollY()
                meshScrollY = totalScrollY % numHeight
                offRowIndex = abs(totalScrollY / numHeight).toInt()
                XLog.i("offRowIndex--->$offLineIndex")
                XLog.i("offLineIndex--->$offRowIndex")
                invalidate()
            }
        }
        mLastX = x
        mLastY = y
        flingGestureDetector.onTouchEvent(event)
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val startRowsIndex = offRowIndex
        val startLinesIndex = offLineIndex
        //绘制期号
        canvas.saveAndRestore {
            canvas.translate(0f, numHeight)
            canvas.translate(0f, meshScrollY)
            canvas.saveAndRestore {
                for (i in 0..displayRowNum) {
                    canvas.drawLine(0f, 0f, dateWidth, 0f, mMeshPaint)
                    canvas.translate(0f, numHeight)
                }
            }
        }
        canvas.saveAndRestore {
            canvas.translate(dateWidth, 0f)
            canvas.translate(meshScrollX, 0f)
            canvas.saveAndRestore {
                for (i in 0..displayLineNum) {
                    if (i == 0) {
                        canvas.saveAndRestore {
                            canvas.clipRect(abs(meshScrollX) - 1, 0f, numWidth, height.toFloat())
                            canvas.drawLine(0f, 0f, 0f, height.toFloat(), mMeshPaint)
                        }
                    } else
                        canvas.drawLine(0f, 0f, 0f, height.toFloat(), mMeshPaint)
                    canvas.translate(numWidth, 0f)
                }
            }
        }

        canvas.saveAndRestore {
            canvas.translate(dateWidth, numHeight)
            //绘制网格
            drawMesh(canvas)
            //绘制号码
            drawNum(canvas, startRowsIndex, startLinesIndex)
        }
    }

    private fun drawMesh(canvas: Canvas) {
        canvas.saveAndRestore {
            canvas.translate(meshScrollX, 0f)
            canvas.saveAndRestore {
                for (i in 0..displayLineNum) {
                    if (i == 0) {
                        canvas.saveAndRestore {
                            canvas.clipRect(abs(meshScrollX) - 1, 0f, numWidth, height.toFloat())
                            canvas.drawLine(0f, 0f, 0f, height.toFloat(), mMeshPaint)
                        }
                    } else
                        canvas.drawLine(0f, 0f, 0f, height.toFloat(), mMeshPaint)
                    canvas.translate(numWidth, 0f)
                }
            }
        }
        canvas.saveAndRestore {
            canvas.translate(0f, meshScrollY)
            canvas.saveAndRestore {
                for (i in 0..displayRowNum) {
                    if (i == 0) {
                        canvas.saveAndRestore {
                            canvas.clipRect(0f, abs(meshScrollY) - 1, width.toFloat(), numHeight)
                            canvas.drawLine(0f, 0f, width.toFloat(), 0f, mMeshPaint)
                        }
                    } else
                        canvas.drawLine(0f, 0f, width.toFloat(), 0f, mMeshPaint)
                    canvas.translate(0f, numHeight)
                }
            }
        }
    }

    private fun drawNum(canvas: Canvas, startRowsIndex: Int, startLinesIndex: Int) {
        canvas.saveAndRestore {
            canvas.translate(meshScrollX, meshScrollY)
            val endRowsIndex = if (startRowsIndex + displayRowNum >= dataList.size) dataList.size - 1 else startRowsIndex + displayRowNum
            for (rowIndex in startRowsIndex..endRowsIndex) {
                val numText = dataList[rowIndex]
                //绘制一行中的数字
                if(rowIndex == startRowsIndex){
                    canvas.saveAndRestore {
                        canvas.clipRect(0f, abs(meshScrollY) - 1, width.toFloat(), numHeight)
                        drawRowNum(canvas, startLinesIndex, numText)
                    }
                }else{
                    drawRowNum(canvas, startLinesIndex, numText)
                }
                canvas.translate(0f, numHeight)
            }
        }
    }

    private fun drawRowNum(canvas: Canvas, startLinesIndex: Int, numText: LotteryNumData) {
        canvas.saveAndRestore {
            //绘制前五位
            val endLinesIndex = if (startLinesIndex + displayLineNum < numText.lotteryNumFrontList.size) startLinesIndex + displayLineNum else numText.lotteryNumFrontList.size - 1
            for (numIndex in startLinesIndex..endLinesIndex) {
                val num = numText.lotteryNumFrontList[numIndex]
                mNumTextPaint.getTextBounds(num.num, 0, num.num.length, tempRect)
                if (numIndex == startLinesIndex) {
                    canvas.saveAndRestore {
                        canvas.clipRect(abs(meshScrollX), 0f, numWidth, numHeight)
                        realDrawNum(num, canvas)
                    }
                } else {
                    realDrawNum(num, canvas)
                }
                canvas.translate(numWidth, 0f)
            }
            //绘制后两位
            if (startLinesIndex + displayLineNum >= numText.lotteryNumFrontList.size) {
                val startIndex = if (startLinesIndex < numText.lotteryNumFrontList.size) 0 else startLinesIndex - numText.lotteryNumFrontList.size
                val endIndex = startLinesIndex + displayLineNum - numText.lotteryNumFrontList.size
                for (numIndex in startIndex..endIndex) {
                    if (numIndex < numText.lotteryNumBackList.size) {
                        val num = numText.lotteryNumBackList[numIndex]
                        mNumTextPaint.getTextBounds(num.num, 0, num.num.length, tempRect)
                        if (numIndex == startIndex && startLinesIndex >= numText.lotteryNumFrontList.size) {
                            canvas.saveAndRestore {
                                canvas.clipRect(abs(meshScrollX), 0f, numWidth, numHeight)
                                realDrawNum(num, canvas)
                            }
                        } else {
                            realDrawNum(num, canvas)
                        }
                        canvas.translate(numWidth, 0f)
                    }
                }
            }
        }
    }

    private fun realDrawNum(num: LotteryNum, canvas: Canvas) {
        num.isLottery.doTrue {
            mNumTextPaint.color = context.getColorResource(R.color.colorAccent)
            canvas.drawCircle(numWidth / 2, numHeight / 2, min(numWidth / 2f * 0.8f, numHeight / 2f * 0.8f), mNumTextPaint)
        }
        mNumTextPaint.color = context.getColorResource(if (num.isLottery) R.color.white else R.color.black)
        canvas.drawText(num.num, (numWidth) / 2, (numHeight + tempRect.height()) / 2, mNumTextPaint)
    }


    data class LotteryNumData(val date: String, val lotteryNumFrontList: MutableList<LotteryNum>, val lotteryNumBackList: MutableList<LotteryNum>)
    data class LotteryNum(val num: String, val isLottery: Boolean)

    inner class ScrollGestureListener : GestureDetector.SimpleOnGestureListener() {
        //刚刚手指接触到触摸屏的那一刹那，就是触的那一下。
        override fun onDown(e: MotionEvent?): Boolean {
            return super.onDown(e)
        }

        //手指离开view那一瞬间执行
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            return super.onSingleTapUp(e)
        }

        //手指在屏幕上滑动
        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
//            XLog.i("GestureListener-----onScroll---->")
            //当我们手指向下滑动的是表示负数,向上滑动是正数,这个数 distance 是表示距离
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        //用户按下触摸屏、快速拖动后松开(滑动的比onScroll快)
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            //velocity 这个词表示的是速度的意思
            XLog.i("GestureListener-----onFling---->velocityX = $velocityX ，velocityY = $velocityY")
            //循环执行runnable
            mFuture = mExecutor.scheduleWithFixedDelay(
                    InertiaScrollTimerTask(velocityX.toInt(), velocityY.toInt()), 0, 10L,
                    TimeUnit.MILLISECONDS)
            return true
        }
    }


    private fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
        }
    }

    inner class InertiaScrollTimerTask(private var velocityX: Int, private var velocityY: Int) : Runnable {
        private val TAG = "InertiaTimerTask"
        private var realVelocityX = Integer.MAX_VALUE.toFloat()
        private var realVelocityY = Integer.MAX_VALUE.toFloat()
        override fun run() {
//            Log.i(TAG, "velocityY--->$velocityY")
//            Log.i(TAG, "realVelocityY--->$realVelocityY")
            //最大拖动速度2000
            checkVelocityX()
            checkVelocityY()
            if (abs(realVelocityX) in 0.0f..20f && abs(realVelocityY) in 0.0f..20f) {
                Log.i(TAG, "WHAT_SMOOTH_SCROLL_INERTIA--->")
                cancelFuture()
                return
            }
            val x = (realVelocityX * 10f / 1000f).toInt()
            totalScrollX += x
            checkScrollX()
            meshScrollX = totalScrollX % numWidth
            offLineIndex = abs(totalScrollX / numWidth).toInt()

            val y = (realVelocityY * 10f / 1000f).toInt()
            totalScrollY += y
            checkScrollY()
            meshScrollY = totalScrollY % numHeight
            offRowIndex = abs(totalScrollY / numHeight).toInt()

            if (realVelocityX < 0.0f) {
                realVelocityX += 20f
            } else {
                realVelocityX -= 20f
            }

            if (realVelocityY < 0.0f) {
                realVelocityY += 20f
            } else {
                realVelocityY -= 20f
            }
            postInvalidate()
        }

        private fun checkVelocityX() {
            if (realVelocityX == Integer.MAX_VALUE.toFloat()) {
                realVelocityX = if (abs(velocityX) > MAX_SCROLL_VELOCITY) {
                    if (velocityX > 0.0f) {
                        MAX_SCROLL_VELOCITY
                    } else {
                        -MAX_SCROLL_VELOCITY
                    }
                } else velocityX.toFloat()
            }
        }

        private fun checkVelocityY() {
            if (realVelocityY == Integer.MAX_VALUE.toFloat()) {
                realVelocityY = if (abs(velocityY) > MAX_SCROLL_VELOCITY) {
                    if (velocityY > 0.0f) {
                        MAX_SCROLL_VELOCITY
                    } else {
                        -MAX_SCROLL_VELOCITY
                    }
                } else velocityY.toFloat()
            }
        }
    }

    private val smoothScrollHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    companion object {
        const val SCROLL_STATE_IDLE = 10000     // 停止滚动
        const val SCROLL_STATE_DRAGGING = 10001 // 用户按住滚轮拖拽
        const val SCROLL_STATE_SCROLLING = 10002 // 依靠惯性滚动
        const val MAX_SCROLL_VELOCITY = 2000f
    }
}