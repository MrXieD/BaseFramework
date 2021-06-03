package com.example.baseframework.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.baseframework.R
import com.example.baseframework.ex.*
import com.example.baseframework.log.XLog
import com.example.baseframework.utils.StringUtils
import com.example.imlotterytool.db.table.*
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
 * 4、中奖号码连线（暂缓）
 * 5、需要显示期号
 * 6、双指缩小和放大、双击放大或缩小（暂缓）
 * 7、显示滚动条
 * 8、位数颜色不一样（单数一个颜色，双数一个颜色）
 */
class LotteryNumDisplayView : View {
    //显示界面行数
    private var displayRowNum = 15

    //显示界面列数
    private var displayLineNum = 10

    //期号宽度
    private var dateWidth = context.dip2px(64f)

    //期号高度
    private var dateHeight = context.dip2px(64f)


    //数字方框宽度
    private var numWidth: Float = context.dip2px(16f)

    //数字方框宽度
    private var numHeight: Float = context.dip2px(16f)

    //滚动条长度和宽度
    private val scrollBarWidth: Float = context.dip2px(4f)
    private val scrollBarHeight: Float = context.dip2px(24f)

    //字体大小
    private var numTextSize = 16f

    //起始行Index
    @Volatile
    private var offStartRowIndex = 0

    //起始行Index
    @Volatile
    private var offStartLineIndex = 0

    //数据List
    private var dataList = mutableListOf<LotteryItem>()
    private var numTextList = mutableListOf<String>()

    //总列数
    private var totalLines = 0

    //总行数
    private var totalRows = 0

    //数字画笔
    private val mNumTextPaint: NumTextCenterPaint = NumTextCenterPaint()

    //期号画笔
    private val mIssuesTextPaint: NumTextCenterPaint = NumTextCenterPaint()

    //网格画笔
    private val mMeshPaint: Paint = Paint()

    //加粗网格画笔
    private val mThickMeshPaint: Paint = Paint()


    private val mBackgroundColorPaint1: Paint = Paint()
    private val mBackgroundColorPaint2: Paint = Paint()

    //滚动条画笔
    private val mScrollBarPaint: Paint = Paint()

    //用于计算字体宽高
    private val tempRect = Rect()


    private var mLastX = 0
    private var mLastY = 0

    //网格X轴偏移量
    @Volatile
    private var totalScrollX = 0

    //网格X轴偏移量
    private var meshScrollX = 0f

    @Volatile
    private var totalScrollY = 0

    //网格Y轴偏移量
    private var meshScrollY = 0f

    private var flingGestureDetector: GestureDetector

    private var mExecutor = Executors.newSingleThreadScheduledExecutor()

    private var mFuture: ScheduledFuture<*>? = null

    //缩放检测
    private var scaleGestureDetector: ScaleGestureDetector

    //缩放倍率
    private var scaleFactor = 1f

    //缩放x中心点
    private var cX = 1f

    //缩放y中心点
    private var cY = 1f

    //是否强制不准滑动
    private var forceIntercptMove = false

    //是否可以斜角滑动
    private var isSlantMove = false

    //是否显示折线
    private var isShowBrokenLine = false

    //折线点位List(以列优先的形式存储point)
    private val mBrokenLinePointsList = mutableListOf<MutableList<PointF>>()

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


        mThickMeshPaint.isAntiAlias = true
        mThickMeshPaint.color = context.getColorResource(R.color.black)
        mThickMeshPaint.style = Paint.Style.STROKE
        mThickMeshPaint.strokeWidth = 3f


        mScrollBarPaint.isAntiAlias = true
        mScrollBarPaint.color = context.getColorResource(R.color.color_outerTextColor)
        mScrollBarPaint.style = Paint.Style.FILL
        //设置为圆角
        mScrollBarPaint.strokeCap = Paint.Cap.ROUND


        mBackgroundColorPaint1.isAntiAlias = true
        mBackgroundColorPaint1.color = context.getColorResource(R.color.color_58ACFA)
        mBackgroundColorPaint1.style = Paint.Style.FILL

        mBackgroundColorPaint2.isAntiAlias = true
        mBackgroundColorPaint2.color = context.getColorResource(R.color.color_81DAF5)
        mBackgroundColorPaint2.style = Paint.Style.FILL

        mNumTextPaint.isAntiAlias = true
        mNumTextPaint.color = context.getColorResource(R.color.black)
        mNumTextPaint.textSize = context.sp2px(numTextSize)
        mNumTextPaint.textAlign = Paint.Align.CENTER

        mIssuesTextPaint.isAntiAlias = true
        mIssuesTextPaint.color = context.getColorResource(R.color.black)
        mIssuesTextPaint.textSize = context.sp2px(numTextSize)
        mIssuesTextPaint.textAlign = Paint.Align.CENTER
        //手势检测
        flingGestureDetector = GestureDetector(context, ScrollGestureListener())
        flingGestureDetector.setIsLongpressEnabled(false)
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    /**
     * lotteryData -> Lottery Data Info
     * numTextList -> 头行数字显示
     */
    fun refreshData(lotteryData: List<LotteryItem>, numTextList: List<String>) {
        if (lotteryData.isEmpty()) return
        numTextList.isNotEmpty().doTrue {
            this.numTextList.clear()
            this.numTextList.addAll(numTextList)
        }
        dataList.clear()
        dataList.addAll(lotteryData)
        totalRows = dataList.size
        totalLines = this.numTextList.size
        measureNumWH()
        totalScrollX = 0
        totalScrollY = 0
//        requestLayout()
        //由于requestLayout()时，VIew的w,h都没有改变，并且也没有执行任何动画，所以onDraw函数没有被执行

        invalidate()
    }

    fun setItemPosition(position: Int) {
        if (position > dataList.size) return
        totalScrollX = 0
        val realPosition = if (position + displayRowNum >= dataList.size) dataList.size - displayRowNum else position
        totalScrollY = -(realPosition * numHeight).toInt()
        mLastX = 0
        mLastY = totalScrollY
        invalidate()
    }

    //设置彩票有好多位，用于每一位（个，十，百加粗纵列分割线）
    var mBitCount: Int = -1
        set(value) {
            if (value > 0) {
                field = value
                if(!isShowBrokenLine) return
                mBrokenLinePointsList.clear()
                for (i in 0 until value) {
                    val pointList = mutableListOf<PointF>()
                    for (j in 0..displayRowNum + 2) {
                        pointList.add(PointF())
                    }
                    mBrokenLinePointsList.add(pointList)
                }
            }
        }


    private fun initTypedArray(attrs: AttributeSet?) {

    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        measureNumWH()
    }

    private fun measureNumWH() {
        if (dataList.isEmpty()) return

        numWidth = (width - dateWidth) / displayLineNum
        //
        numHeight = height / (displayRowNum + 1f)

        dateHeight = numHeight
        //画笔字体自适应计算
        var testString = ""
        dataList[0].numbers.forEach {
            if (it.num.length > testString.length && StringUtils.isNumeric(it.num)) {
                testString = it.num
            }
        }
        autoMeasureTextSizeToPaint(testString, 0.5f, numWidth, mNumTextPaint)
        autoMeasureTextSizeToPaint(dataList[0].issues, 0.8f, dateWidth, mIssuesTextPaint)
    }

    private fun autoMeasureTextSizeToPaint(
        testString: String, textScale: Float, formWidth: Float, targetPaint:
        NumTextCenterPaint
    ) {
        val nowWidth = targetPaint.measureText(testString)
        val maxWidth = min(formWidth * textScale, formWidth * textScale)
        val scale = nowWidth / targetPaint.textSize
        val newTextSize = maxWidth / scale
        targetPaint.textSize = newTextSize
        targetPaint.textVerDistance =
            (targetPaint.fontMetrics.bottom - targetPaint.fontMetrics.top) / 2 - targetPaint.fontMetrics.bottom

    }

    private fun checkScrollX(): Boolean {
        if (totalScrollX >= 0 || totalLines * numWidth + dateWidth < width) {
            totalScrollX = 0
            return false
        } else if (abs(totalScrollX) + width >= totalLines * numWidth + dateWidth) {
            //这里的+1 之所以要+1是因为需要对float转int时损失的精度作为补偿
            totalScrollX = (-(totalLines * numWidth + dateWidth - width + 1)).toInt()
            return false
        }
        return true
    }

    private fun checkScrollY(): Boolean {
        if (totalScrollY >= 0 || totalRows * numHeight + dateHeight < height) {
            totalScrollY = 0
            return false
        } else {
            if (abs(totalScrollY) + height >= totalRows * numHeight + dateHeight) {
                totalScrollY = (-(totalRows * numHeight + dateHeight - height)).toInt()
                return false
            }
        }
        return true
    }

    @SuppressLint("LongLogTag")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isDown = true
                forceIntercptMove = false
                //按下时停止滚动
                cancelFuture()
            }
            MotionEvent.ACTION_MOVE -> {
                val deltaX: Int = x - mLastX
                val deltaY: Int = y - mLastY
                if (!forceIntercptMove && event.pointerCount == 1) {
                    if (isSlantMove) {
                        totalScrollX += deltaX
                        checkScrollX()
                        totalScrollY += deltaY
                        checkScrollY()
                    } else {
                        if (abs(deltaX) >= abs(deltaY)) {
                            totalScrollX += deltaX
                            checkScrollX()
                        } else {
                            totalScrollY += deltaY
                            checkScrollY()
                        }
                    }
                    if (scaleFactor > 1) {
                        scaleCenterChange(deltaX, deltaY)
                    }
                }
                invalidate()
            }
            //当有多个手指情况下，有手指抬起时候，这时候此触碰系列不准滑动(不然有些手指抬起后画面位置要抖动)，
            // 直到下一次重新触发actionDown事件
            MotionEvent.ACTION_POINTER_UP -> {
                forceIntercptMove = true
            }
            MotionEvent.ACTION_UP -> {
                isDown = false
                invalidate()
            }
        }
        mLastX = x
        mLastY = y
        flingGestureDetector.onTouchEvent(event)
//        scaleGestureDetector.onTouchEvent(event)
        return true
    }

    /*
        当滑动到边界且有放大时，将缩放中心移动到边界，不然看不到边界的东西
     */
    private fun scaleCenterChange(deltaX: Int, deltaY: Int) {
        //x方向
        if (totalScrollX == 0) {//最左边
            if (deltaX > 0 && (cX > 0)) {
                cX -= deltaX
                cX = if (cX <= 0) 0f else cX
            }
        } else if (totalScrollX == (-(totalLines * numWidth + dateWidth - width)).toInt()) {//最右边
            if (deltaX < 0 && (cX < width)) {
                cX -= deltaX
                cX = if (cX >= width) width.toFloat() else cX
            }
        }

        //y方向
        if (totalScrollY == 0) {//最上边
            if (deltaY > 0 && cY > 0) {
                cY -= deltaY
                cY = if (cY <= 0) 0f else cY
            }
        } else if (totalScrollY == (-(totalRows * numHeight + numHeight - height)).toInt()) {//最下边
            if (deltaY < 0 && cY <= height) {
                cY -= deltaY
                cY = if (cY >= height) height.toFloat() else cY
            }
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (dataList.isEmpty()) return
        meshScrollX = totalScrollX % numWidth
        offStartLineIndex = abs(totalScrollX / numWidth).toInt()
        meshScrollY = totalScrollY % numHeight
        offStartRowIndex = abs(totalScrollY / numHeight).toInt()

        val startRowsIndex = offStartRowIndex
        val startLinesIndex = offStartLineIndex
        val currScrollY = totalScrollY

        canvas.saveAndRestore {
            canvas.drawLine(dateWidth, 0f, dateWidth, numHeight, mThickMeshPaint)
            canvas.drawLine(0f, numHeight, dateWidth, numHeight, mThickMeshPaint)
            val date = "期号"
            mNumTextPaint.getTextBounds(date, 0, date.length, tempRect)
            mNumTextPaint.color = context.getColorResource(R.color.black)
            drawText(canvas, date, dateWidth, numHeight, mIssuesTextPaint)
            //绘制期号Title
            canvas.saveAndRestore {
                canvas.translate(0f, numHeight)
                canvas.translate(0f, meshScrollY)
                canvas.saveAndRestore {
                    val endRowsIndex =
                        if (startRowsIndex + displayRowNum >= dataList.size) dataList.size - 1 else startRowsIndex + displayRowNum
                    for (i in startRowsIndex..endRowsIndex) {
                        canvas.saveAndRestore {
                            if (i == startRowsIndex) {
                                canvas.clipRect(
                                    0f, abs(meshScrollY), dateWidth + mThickMeshPaint.strokeWidth / 2,
                                    numHeight
                                )
                            }
                            val paint = if (i % 5 == 0) mThickMeshPaint else mMeshPaint
                            canvas.drawLine(0f, 0f, dateWidth, 0f, paint)
                            canvas.drawLine(dateWidth, 0f, dateWidth, numHeight, mThickMeshPaint)
                            val dateNum = dataList[i]
                            mIssuesTextPaint.getTextBounds(dateNum.issues, 0, dateNum.issues.length, tempRect)
                            mIssuesTextPaint.color = context.getColorResource(R.color.black)
                            drawText(canvas, dateNum.issues, dateWidth, numHeight, mIssuesTextPaint)
                        }
                        canvas.translate(0f, numHeight)
                    }
                }
            }
            //绘制数字Title
            canvas.saveAndRestore {
                canvas.translate(dateWidth, 0f)
                canvas.translate(meshScrollX, 0f)
                canvas.saveAndRestore {
                    val endLinesIndex =
                        if (startLinesIndex + displayLineNum < numTextList.size) startLinesIndex + displayLineNum else numTextList.size - 1
                    for (i in startLinesIndex..endLinesIndex) {
                        canvas.saveAndRestore {
                            if (i == startLinesIndex) {
                                canvas.clipRect(
                                    abs(meshScrollX),
                                    0f,
                                    numWidth,
                                    numHeight + mThickMeshPaint.strokeWidth / 2
                                )
                            }
                            val paint = if (mBitCount > 0) {
                                if (i % mBitCount == 0) mThickMeshPaint else
                                    mMeshPaint
                            } else {
                                mMeshPaint
                            }
//                            val backgroundColorIndex = i / mBitCount
//                            canvas.drawRect(
//                                marginInterval, marginInterval, numWidth-marginInterval, numHeight-marginInterval, if (backgroundColorIndex % 2 == 0) mBackgroundColorPaint1
//                                else mBackgroundColorPaint2
//                            )
                            canvas.drawLine(0f, 0f, 0f, numHeight, paint)
                            canvas.drawLine(0f, numHeight, numWidth, numHeight, mThickMeshPaint)
                            val numText = numTextList[i]
                            mNumTextPaint.getTextBounds(numText, 0, numText.length, tempRect)
                            mNumTextPaint.color = context.getColorResource(R.color.black)
                            drawText(canvas, numText, numWidth, numHeight, mNumTextPaint)
                        }
                        canvas.translate(numWidth, 0f)
                    }
                }
            }


            canvas.saveAndRestore {
                canvas.translate(dateWidth, numHeight)
                //绘制号码
                drawNum(canvas, startRowsIndex, startLinesIndex)
                //绘制网格
                drawMesh(startRowsIndex, startLinesIndex, canvas)
            }
        }
        //绘制折线
        if (isShowBrokenLine) {
            buildBrokenLinePoint(startRowsIndex, startLinesIndex)
            brokenPath.reset()
            val list = mBrokenLinePointsList[0]
            for (i in list.indices) {
                val point = list[i]
                if (point.x == 0f && point.y == 0f) continue
                if (i == 0) {
                    brokenPath.moveTo(point.x, point.y)
                } else {
                    brokenPath.lineTo(point.x, point.y)
                }
            }
            canvas.saveAndRestore {
                canvas.clipRect(
                    dateWidth,
                    numHeight,
                    width.toFloat(),
                    height.toFloat()
                )
                canvas.drawPath(brokenPath, mThickMeshPaint)
            }

        }
        //绘制滚动条,带有一定透明度，暂不支持触控滚动条
        mScrollBarPaint.alpha = if (isScroll || isDown) 128 else 192
        val totalHeight = totalRows * numHeight + dateHeight - height
        val scrollBarPosY = abs(currScrollY) / totalHeight * (height - numHeight - scrollBarHeight) + numHeight
        canvas.drawRect(
            width - scrollBarWidth,
            scrollBarPosY,
            width.toFloat(),
            scrollBarPosY + scrollBarHeight,
            mScrollBarPaint
        )
    }

    private fun buildBrokenLinePoint(startRowsIndex: Int, startLinesIndex: Int) {
        val endRowsIndex =
            if (startRowsIndex + displayRowNum + 1 >= dataList.size) dataList.size - 1 else startRowsIndex + displayRowNum + 1
        val realStartRowIndex = if (startRowsIndex == 0) startRowsIndex else startRowsIndex - 1
        for (rowIndex in realStartRowIndex..endRowsIndex) {
            val numTextList = dataList[rowIndex].numbers
            val endLinesIndex =
                if (startLinesIndex + displayLineNum < numTextList.size) startLinesIndex + displayLineNum else numTextList.size - 1
            for (numLineIndex in startLinesIndex..endLinesIndex) {
                val num = numTextList[numLineIndex]
                (num.ballType > 0).doTrue {
                    val pointIndexOfRow = rowIndex - realStartRowIndex
                    val pointIndexOfLine = numLineIndex / mBitCount
                    val point = mBrokenLinePointsList[pointIndexOfLine][pointIndexOfRow]
                    if(endRowsIndex == dataList.size - 1 && rowIndex - realStartRowIndex > displayRowNum+1) {
                        //绝对坐标
                        point.set(0f,0f)
                    }else{
                        //绝对坐标
                        point.x = (numLineIndex - startLinesIndex) * numWidth + numWidth / 2 + dateWidth+ meshScrollX
                        point.y = (rowIndex - startRowsIndex) * numHeight + numHeight / 2+ numHeight+ meshScrollY
                    }

                }
            }
        }
    }

    private val brokenPath = Path()
    private fun drawMesh(startRowsIndex: Int, startLinesIndex: Int, canvas: Canvas) {
        canvas.saveAndRestore {
            canvas.translate(meshScrollX, 0f)
            canvas.saveAndRestore {
                for (i in 0..displayLineNum) {
                    val paint = if (mBitCount > 0) {
                        if ((startLinesIndex + i) % mBitCount == 0) mThickMeshPaint else
                            mMeshPaint
                    } else {
                        mMeshPaint
                    }

                    if (i == 0) {
                        canvas.saveAndRestore {
                            canvas.clipRect(abs(meshScrollX), 0f, numWidth, height.toFloat())
                            canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)
                        }
                    } else canvas.drawLine(0f, 0f, 0f, height.toFloat(), paint)
                    canvas.translate(numWidth, 0f)
                }
            }
        }
        canvas.saveAndRestore {
            canvas.translate(0f, meshScrollY)
            canvas.saveAndRestore {
                for (i in 0..displayRowNum) {
                    val paint = if ((startRowsIndex + i) % 5 == 0) mThickMeshPaint else mMeshPaint
                    if (i == 0) {
                        canvas.saveAndRestore {
                            canvas.clipRect(0f, abs(meshScrollY), width.toFloat(), numHeight)
                            canvas.drawLine(0f, 0f, width.toFloat(), 0f, paint)
                        }
                    } else canvas.drawLine(0f, 0f, width.toFloat(), 0f, paint)
                    canvas.translate(0f, numHeight)
                }
            }
        }
    }

    private fun drawNum(canvas: Canvas, startRowsIndex: Int, startLinesIndex: Int) {

        canvas.saveAndRestore {
            canvas.translate(meshScrollX, meshScrollY)
            val endRowsIndex =
                if (startRowsIndex + displayRowNum >= dataList.size) dataList.size - 1 else startRowsIndex + displayRowNum
            for (rowIndex in startRowsIndex..endRowsIndex) {
                val numText = dataList[rowIndex]
                //绘制一行中的数字
                canvas.saveAndRestore {
                    if (rowIndex == startRowsIndex) {
                        canvas.clipRect(
                            0f,
                            abs(meshScrollY) + mThickMeshPaint.strokeWidth / 2,
                            width.toFloat(),
                            numHeight
                        )
                    }
                    drawLinesNum(canvas, startLinesIndex, numText)
                }
                canvas.translate(0f, numHeight)
            }
        }
    }

    //一列
    private fun drawLinesNum(canvas: Canvas, startLinesIndex: Int, numDataOneDate: LotteryItem) {
        val list = numDataOneDate.numbers
        canvas.saveAndRestore {
            val endLinesIndex =
                if (startLinesIndex + displayLineNum < list.size) startLinesIndex + displayLineNum else list.size - 1
            for (numIndex in startLinesIndex..endLinesIndex) {
                val num = list[numIndex]
                canvas.saveAndRestore {
                    if (numIndex == startLinesIndex) {
                        canvas.clipRect(abs(meshScrollX) + mThickMeshPaint.strokeWidth / 2, 0f, numWidth, numHeight)
                    }
                    val backgroundColorIndex = (numIndex) / mBitCount
                    canvas.drawRect(
                        marginInterval,
                        marginInterval,
                        numWidth - marginInterval,
                        numHeight - marginInterval,
                        if (backgroundColorIndex % 2 == 0) mBackgroundColorPaint1 else mBackgroundColorPaint2
                    )
                    realDrawNum(num, canvas)
                }
                canvas.translate(numWidth, 0f)
            }
        }
    }

    private var marginInterval = context.dip2px(1.5f)
    private fun realDrawNum(num: OneLotteryNum, canvas: Canvas) {
        mNumTextPaint.getTextBounds(num.num, 0, num.num.length, tempRect)
        (num.ballType > 0).doIf({
            mNumTextPaint.color = if (num.ballType == BLUE_BALL_TYPE) {
                Color.BLUE
            } else {
                Color.RED
            }
            canvas.drawCircle(
                numWidth / 2,
                numHeight / 2,
                min(numWidth / 2f * 0.8f, numHeight / 2f * 0.8f),
                mNumTextPaint
            )
            mNumTextPaint.color = Color.WHITE
            drawText(canvas, num.num, numWidth, numHeight, mNumTextPaint)
        }, {
            mNumTextPaint.color = Color.GRAY
            drawText(canvas, num.num, numWidth, numHeight, mNumTextPaint)
        })
    }

    private fun drawText(
        canvas: Canvas,
        text: String,
        latticeWidth: Float,
        latticeHeight: Float,
        paint: NumTextCenterPaint
    ) = canvas.drawText(text, latticeWidth / 2, latticeHeight / 2 + paint.textVerDistance, paint)


    private var isScroll = false
    private var isDown = false
    /**
     * issue -> 期号
     *
     */
//    data class OneDateLotteryData(val issue: String, val numList: MutableList<OneLotteryNum>)

    /**
     * isLottery -->是否中奖号码
     *
     */
//    data class OneLotteryNum(val num: String, val isLottery: Boolean, val ballType: Int = -1)

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
//            Log.i("GestureListener-----onScroll---->")
            //当我们手指向下滑动的是表示负数,向上滑动是正数,这个数 distance 是表示距离
            return super.onScroll(e1, e2, distanceX, distanceY)
        }

        //用户按下触摸屏、快速拖动后松开(滑动的比onScroll快)
        override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
            //velocity 这个词表示的是速度的意思
//            Log.i(TAG,"GestureListener-----onFling---->velocityX = $velocityX ，velocityY = $velocityY")
            //循环执行runnable
            var vX = velocityX
            var vY = velocityY
            if (!isSlantMove) {
                if (abs(velocityX) >= abs(velocityY)) {
                    vX = velocityX
                    vY = 0f
                } else {
                    vX = 0f
                    vY = velocityY
                }
            }
            if (!forceIntercptMove) {
                isScroll = true
                mFuture = mExecutor.scheduleWithFixedDelay(
                    InertiaScrollTimerTask(vX.toInt(), vY.toInt()), 0, 7L,
                    TimeUnit.MILLISECONDS
                )
            }
            return true
        }
    }


    private fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
            isScroll = false
            postInvalidate()
        }
    }

    inner class InertiaScrollTimerTask(private var velocityX: Int, private var velocityY: Int) : Runnable {
        private val TAG = "InertiaTimerTask"
        private var realVelocityX = Integer.MAX_VALUE.toFloat()
        private var realVelocityY = Integer.MAX_VALUE.toFloat()
        override fun run() {
            //最大拖动速度2000
            checkVelocityX()
            checkVelocityY()
            if (abs(realVelocityX) in 0.0f..20f && abs(realVelocityY) in 0.0f..20f) {
                cancelFuture()
                return
            }
            val x = (realVelocityX * 10f / 1000f).toInt()
            totalScrollX += x
            if (!checkScrollX()) {
                realVelocityX = 0f
            }
            val y = (realVelocityY * 10f / 1000f).toInt()
            totalScrollY += y
            if (!checkScrollY()) {
                realVelocityY = 0f
            }
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

    inner class ScaleListener : ScaleGestureDetector.OnScaleGestureListener {
        override fun onScaleBegin(detector: ScaleGestureDetector?): Boolean {
            return true
        }

        override fun onScaleEnd(detector: ScaleGestureDetector?) {
        }

        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            if (scaleFactor > 2) scaleFactor = 2f//Limit max
            else if (scaleFactor < 1f) scaleFactor = 1f;//limit min
            scaleFactor = (((scaleFactor * 100))) / 100;//jitter-protection
            cX = detector.focusX
            cY = detector.focusY
//            Log.e(TAG, "onScale: $scaleFactor, $cX,$cY")
            return true
        }

    }

    //缩放画布
    private fun scaleCanvas(canvas: Canvas) = canvas.scale(scaleFactor, scaleFactor, cX, cY)


    companion object {
        const val SCROLL_STATE_IDLE = 10000     // 停止滚动
        const val SCROLL_STATE_DRAGGING = 10001 // 用户按住滚轮拖拽
        const val SCROLL_STATE_SCROLLING = 10002 // 依靠惯性滚动
        const val MAX_SCROLL_VELOCITY = 2000f
        private val TAG = "LotteryNumDisplayView"
    }

    internal class NumTextCenterPaint() : Paint() {
        //用来计算文字绘制基线的辅助距离，和画笔字体大小有关系
        //原理可以参考:https://www.jianshu.com/p/8b97627b21c4
        var textVerDistance: Float = 0f
    }
}