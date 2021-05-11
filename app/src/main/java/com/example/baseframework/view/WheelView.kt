package com.example.baseframework.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.example.baseframework.R
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * 可循环滚动View
 */
class WheelView : View {
    val TAG = "WheelView"

    init {
        flingGestureDetector = GestureDetector(context, GestureListener())
        flingGestureDetector.setIsLongpressEnabled(false)
    }

    constructor(context: Context) : super(context) {
        initTypedArray(null)
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

    private fun initTypedArray(attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.WheelView)
        if (typedArray != null) {
            textSize = typedArray.getInteger(
                R.styleable.WheelView_awv_textsize,
                (Resources.getSystem().displayMetrics.density * 15).toInt()
            )
            textSize = (Resources.getSystem().displayMetrics.density * textSize).toInt()
            centerTextColor = typedArray.getInteger(
                R.styleable.WheelView_awv_centerTextColor,
                context.resources.getColor(R.color.color_centerTextColor)
            )
            outerTextColor = typedArray.getInteger(
                R.styleable.WheelView_awv_outerTextColor,
                context.resources.getColor(R.color.color_outerTextColor)
            )
            dividerColor = typedArray.getInteger(
                R.styleable.WheelView_awv_dividerTextColor,
                context.resources.getColor(R.color.color_dividerTextColor)
            )
            itemsVisibleCount = typedArray.getInteger(
                R.styleable.WheelView_awv_itemsVisibleCount,
                DEFAULT_VISIBIE_ITEMS
            )
            if (itemsVisibleCount % 2 == 0) {
                itemsVisibleCount = DEFAULT_VISIBIE_ITEMS
            }
            isLoop = typedArray.getBoolean(R.styleable.WheelView_awv_isLoop, true)

            typedArray.recycle()

            outerTextPaint.color = outerTextColor
            outerTextPaint.isAntiAlias = true
            outerTextPaint.typeface = typeface
            outerTextPaint.textSize = textSize.toFloat()

            centerTextPaint.color = centerTextColor
            centerTextPaint.isAntiAlias = true
            centerTextPaint.textScaleX = scaleX
            centerTextPaint.typeface = typeface
            centerTextPaint.textSize = textSize.toFloat()

            paintIndicator.color = dividerColor
            paintIndicator.isAntiAlias = true

        }
    }

    private var outerTextPaint: Paint = Paint()
    private var centerTextPaint: Paint = Paint()

    //绘制分割线
    private var paintIndicator: Paint = Paint()

    private var onItemSelectedListener: OnItemSelectedListener? = null
    private var mOnItemScrollListener: OnItemScrollListener? = null

    @Volatile
    private var mOffset: Int = 0
    private var itemTextHeight: Int = 0
    private var halfCircumference: Int = 0
    private var initPosition: Int = -1
    private var preCurrentIndex: Int = 0

    @Volatile
    private var totalScrollY: Int = 0
    private var isLoop: Boolean = true
    private var itemsVisibleCount: Int = 0
    private val DEFAULT_VISIBIE_ITEMS = 9

    //默认字体
    private var typeface = Typeface.MONOSPACE
    private var textSize: Int = 0

    //文本高度
    private var textHeight: Int = 0

    private var centerTextColor: Int = 0
    private var outerTextColor: Int = 0
    private var dividerColor: Int = 0

    private val SCROLL_STATE_IDLE = 0     // 停止滚动
    private val SCROLL_STATE_SETTING = 1  // 用户设置
    private val SCROLL_STATE_DRAGGING = 2 // 用户按住滚轮拖拽
    private val SCROLL_STATE_SCROLLING = 3 // 依靠惯性滚动

    private var lastScrollState = SCROLL_STATE_IDLE
    private var currentScrollState = SCROLL_STATE_IDLE


    private var flingGestureDetector: GestureDetector
    private var drawingStrings: HashMap<Int, IndexString> = HashMap()

    //绘制中间分割线Y
    private var oneLineY: Float = 0f
    private var twoLineY: Float = 0f

    private val tempRect = Rect()
    private var radius: Int = 0

    private var items: ArrayList<IndexString> = ArrayList()

    private var mLastY: Float = 0f
    private var clickStartTime: Long = 0

    private var mExecutor = Executors.newSingleThreadScheduledExecutor()
    private var mFuture: ScheduledFuture<*>? = null

    private val handler: LoopHandler = LoopHandler(this)

    enum class ACTION {
        CLICK, FLING, DRAG
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (items.isNullOrEmpty()) return
        var maxLenText = ""
        for (i in items.indices) {
            if (maxLenText.length <= items[i].content.length) {
                maxLenText = items[i].content
            }
        }
        maxLenText += "测试"
        centerTextPaint.getTextBounds(maxLenText, 0, maxLenText.length, tempRect)
        textHeight = tempRect.height()
        if (measuredWidth < tempRect.width()) {
            setMeasuredDimension(MeasureSpec.makeMeasureSpec(tempRect.width(), MeasureSpec.EXACTLY), heightMeasureSpec)
        }

        halfCircumference = (measuredHeight * Math.PI / 2).toInt()
        //根据周长来确定实际的selectedItem高度
        itemTextHeight = halfCircumference / (itemsVisibleCount - 1)

        radius = measuredHeight / 2

        oneLineY = (measuredHeight - itemTextHeight) / 2f
        twoLineY = (measuredHeight + itemTextHeight) / 2f

        if (initPosition == -1) {
            initPosition = if (isLoop) {
                //中间
                (items.size + 1) / 2
            } else {
                0
            }
        }
        preCurrentIndex = initPosition

    }


    fun setCenterTextColor(centerTextColor: Int) {
        this.centerTextColor = centerTextColor
        centerTextPaint.color = centerTextColor
    }

    fun setOuterTextColor(outerTextColor: Int) {
        this.outerTextColor = outerTextColor
        outerTextPaint.color = outerTextColor
    }

    fun setDividerColor(dividerColor: Int) {
        this.dividerColor = dividerColor
        paintIndicator.color = dividerColor
    }

    fun setTextTypeface(typeface: Typeface) {
        this.typeface = typeface
        outerTextPaint.typeface = this.typeface
        centerTextPaint.typeface = this.typeface
        requestLayout()
    }

    fun setItemsVisibleCount(visibleNumber: Int) {
        if (visibleNumber % 2 == 0) {
            return
        }
        if (visibleNumber != itemsVisibleCount) {
            itemsVisibleCount = visibleNumber
            drawingStrings.clear()
        }
    }

    /**
     * set not loop
     */
    fun setLoop(loop: Boolean) {
        isLoop = loop
        requestLayout()
    }

    /**
     * set text size in dp
     * @param size
     */
    fun setTextSize(size: Float) {
        if (size > 0.0f) {
            textSize = (context.resources.displayMetrics.density * size).toInt()
            outerTextPaint.textSize = textSize.toFloat()
            centerTextPaint.textSize = textSize.toFloat()
        }
    }

    fun setInitPosition(initPosition: Int) {
        if (initPosition < 0) {
            this.initPosition = 0
        } else {
            if (items.size > initPosition) {
                this.initPosition = initPosition
            }
        }
    }

    fun setOnItemSelectedListener(OnItemSelectedListener: OnItemSelectedListener) {
        onItemSelectedListener = OnItemSelectedListener
    }

    fun setOnItemClick(listener: (Int) -> Unit) {
        onItemSelectedListener = object :
            OnItemSelectedListener {
            override fun onItemSelected(index: Int) {
                listener(index)
            }
        }
    }

    fun setOnItemScrollListener(mOnItemScrollListener: OnItemScrollListener) {
        this.mOnItemScrollListener = mOnItemScrollListener
    }

    fun setItems(items: List<String>?) {
        if (items.isNullOrEmpty()) {
            return
        }
        this.items.clear()
        this.items.addAll(convertData(items))
        requestLayout()
    }

    private fun convertData(items: List<String>): List<IndexString> {
        val data = ArrayList<IndexString>()
        for (i in items.indices) {
            data.add(IndexString(i, items[i]))
        }
        return data
    }


    fun setCurrentPosition(position: Int) {
        if (items.isNullOrEmpty()) {
            return
        }
        val size = items.size
        if (position in 0 until size && position != getSelectedItem()) {
            initPosition = position
            totalScrollY = 0
            mOffset = 0
            changeScrollState(SCROLL_STATE_SETTING)
            invalidate()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val isConsumed = flingGestureDetector.onTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = event.y
                clickStartTime = SystemClock.uptimeMillis()
                cancelFuture()
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                val dy = mLastY - event.y
                mLastY = event.y
                totalScrollY += dy.toInt()
                if (!isLoop) {
                    val top = -initPosition * itemTextHeight
                    val bottom = (items.size - 1 - initPosition) * itemTextHeight
                    if (totalScrollY < top) {
                        totalScrollY = top
                    } else if (totalScrollY > bottom) {
                        totalScrollY = bottom
                    }
                }
                changeScrollState(SCROLL_STATE_DRAGGING)
            }
            else -> {
                if (!isConsumed) {
                    val y = event.y
                    val l = acos(((radius - y) / radius).toDouble()) * radius
                    val circlePosition = ((l + itemTextHeight / 2) / itemTextHeight).toInt()

                    val extraOffset = (totalScrollY % itemTextHeight + itemTextHeight) % itemTextHeight
                    mOffset = ((circlePosition - itemsVisibleCount / 2) * itemTextHeight - extraOffset)

                    if (SystemClock.uptimeMillis() - clickStartTime > 120) {
                        smoothScroll(ACTION.DRAG)
                    } else {
                        smoothScroll(ACTION.CLICK)
                    }
                }
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
        }
        invalidate()
        return true
    }

    private fun smoothScroll(action: ACTION) {
        cancelFuture()
        if (action == ACTION.FLING || action == ACTION.DRAG) {
            val itemHeight = itemTextHeight
            mOffset = ((totalScrollY % itemHeight + itemHeight) % itemHeight)
            if (mOffset.toFloat() > itemHeight / 2.0f) {
                mOffset = (itemHeight - mOffset.toFloat()).toInt()
            } else {
                mOffset = -mOffset
            }
        }
        mFuture = mExecutor.scheduleWithFixedDelay(
            SmoothScrollTimerTask(mOffset),
            0,
            10,
            TimeUnit.MILLISECONDS
        )
        changeScrollState(SCROLL_STATE_SCROLLING)
    }


    private fun cancelFuture() {
        if (mFuture != null && !mFuture!!.isCancelled) {
            mFuture!!.cancel(true)
            mFuture = null
            changeScrollState(SCROLL_STATE_IDLE)
        }
    }


    private fun changeScrollState(scrollState: Int) {
        if (scrollState != currentScrollState && !handler.hasMessages(LoopHandler.WHAT_SMOOTH_SCROLL_INERTIA)) {
            lastScrollState = currentScrollState
            currentScrollState = scrollState
        }
    }


    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (items.isNullOrEmpty()) {
            return
        }
        Log.i(TAG, "totalScrollY--->$totalScrollY")
        val change = totalScrollY / itemTextHeight
        Log.i(TAG, "change--->$change")
        preCurrentIndex = initPosition + change % items.size
        if (!isLoop) {
            if (preCurrentIndex < 0) {
                preCurrentIndex = 0
            }
            if (preCurrentIndex > items.size - 1) {
                preCurrentIndex = items.size - 1
            }
        } else {
            if (preCurrentIndex < 0) {
                preCurrentIndex += items.size
            }
            if (preCurrentIndex > items.size - 1) {
                preCurrentIndex -= items.size
            }
        }
        Log.i(TAG, "preCurrentIndex--->$preCurrentIndex")
        val j2 = totalScrollY % itemTextHeight
        Log.i(TAG, "j2--->$j2")
        var k1 = 0
        while (k1 < itemsVisibleCount) {
            var topIndex = preCurrentIndex - (itemsVisibleCount / 2 - k1)
            when {
                isLoop -> {
                    while (topIndex < 0) {
                        topIndex += items.size
                    }
                    while (topIndex > items.size - 1) {
                        topIndex -= items.size
                    }
                    drawingStrings[k1] = items[topIndex]
                }
                topIndex < 0 -> {
                    drawingStrings[k1] = IndexString()
                }
                topIndex > items.size - 1 -> {
                    drawingStrings[k1] = IndexString()
                }
                else -> {
                    drawingStrings[k1] = items[topIndex]
                }
            }
            k1++
        }
        //绘制分割线
        canvas.drawLine(
            paddingLeft.toFloat(),
            oneLineY,
            measuredWidth.toFloat(),
            oneLineY,
            paintIndicator
        )
        canvas.drawLine(
            paddingLeft.toFloat(),
            twoLineY,
            measuredWidth.toFloat(),
            twoLineY,
            paintIndicator
        )
        var i = 0
        while (i < itemsVisibleCount) {
            canvas.save()
            val itemHeight = itemTextHeight
            val radian = (itemHeight * i - j2) * Math.PI / halfCircumference
            if (radian >= Math.PI || radian <= 0) {
                canvas.restore()
            } else {
                val translateY =
                    (radius.toDouble() - cos(radian) * radius - sin(radian) * itemTextHeight / 2.0f).toFloat()
                canvas.translate(0.0f, translateY)
                canvas.scale(1.0f, sin(radian).toFloat())
                if (translateY <= oneLineY && translateY + itemTextHeight >= oneLineY) {
                    //这部分文字在第一条分割线上
                    canvas.save()
                    canvas.clipRect(0f, 0f, measuredWidth.toFloat(), oneLineY.toInt() - translateY)
                    drawOuterText(canvas, i)
                    canvas.restore()
                    canvas.save()
                    canvas.clipRect(0f, oneLineY.toInt() - translateY, measuredWidth.toFloat(), itemHeight.toFloat())
                    drawCenterText(canvas, i)
                    canvas.restore()
                } else if (translateY <= twoLineY && translateY + itemTextHeight >= twoLineY) {
                    // second divider
                    canvas.save()
                    canvas.clipRect(0f, 0f, measuredWidth.toFloat(), twoLineY.toInt() - translateY)
                    drawCenterText(canvas, i)
                    canvas.restore()
                    canvas.save()
                    canvas.clipRect(0f, twoLineY.toInt() - translateY, measuredWidth.toFloat(), itemHeight.toFloat())
                    drawOuterText(canvas, i)
                    canvas.restore()
                } else if (translateY >= oneLineY && itemTextHeight + translateY <= twoLineY) {
                    // center item
                    canvas.clipRect(0, 0, measuredWidth, itemHeight)
                    drawCenterText(canvas, i)
                } else {
                    // other item
                    canvas.clipRect(0, 0, measuredWidth, itemHeight)
                    drawOuterText(canvas, i)
                }
                canvas.restore()
            }
            i++
        }
        if (currentScrollState != lastScrollState) {
            val oldScrollState = lastScrollState
            lastScrollState = currentScrollState
            if (mOnItemScrollListener != null) {
                mOnItemScrollListener!!.onItemScrollStateChanged(
                    this,
                    getSelectedItem(),
                    oldScrollState,
                    currentScrollState,
                    totalScrollY
                )
            }

        }
        if (currentScrollState == SCROLL_STATE_DRAGGING || currentScrollState == SCROLL_STATE_SCROLLING) {
            if (mOnItemScrollListener != null) {
                mOnItemScrollListener!!.onItemScrolling(
                    this,
                    getSelectedItem(),
                    currentScrollState,
                    totalScrollY
                )
            }
        }
    }

    private fun drawOuterText(canvas: Canvas, position: Int) {
        canvas.drawText(
            drawingStrings[position]!!.content,
            getTextX(drawingStrings[position]!!.content, outerTextPaint, tempRect).toFloat(),
            getDrawingY().toFloat(),
            outerTextPaint
        )
    }

    private fun drawCenterText(canvas: Canvas, position: Int) {
        canvas.drawText(
            drawingStrings[position]!!.content,
            getTextX(drawingStrings[position]!!.content, outerTextPaint, tempRect).toFloat(),
            getDrawingY().toFloat(),
            centerTextPaint
        )
    }

    private fun getDrawingY(): Int =
        if (itemTextHeight > textHeight) itemTextHeight - (itemTextHeight - textHeight) / 2 else itemTextHeight


    // text start drawing position
    private fun getTextX(a: String, paint: Paint, rect: Rect): Int {
        paint.getTextBounds(a, 0, a.length, rect)
        val textWidth = rect.width()
        return (measuredWidth - paddingLeft - textWidth) / 2 + paddingLeft
    }


    private fun getSelectedItem(): Int = preCurrentIndex

    private fun onItemSelected() {
        if (onItemSelectedListener != null) {
            postDelayed({ onItemSelectedListener!!.onItemSelected(getSelectedItem()) }, 100)
        }
    }

    data class IndexString(val index: Int = 0, val content: String = "")

    class LoopHandler(private val wheelView: WheelView) : Handler(Looper.getMainLooper()) {
        companion object {
            const val WHAT_SMOOTH_SCROLL = 2000
            const val WHAT_SMOOTH_SCROLL_INERTIA = 2001
            const val WHAT_ITEM_SELECTED = 3000
        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                WHAT_SMOOTH_SCROLL -> {
                    removeMessages(WHAT_SMOOTH_SCROLL_INERTIA)
                    wheelView.smoothScroll(ACTION.FLING)
                }
                WHAT_ITEM_SELECTED -> wheelView.onItemSelected()
            }
        }
    }

    inner class SmoothScrollTimerTask(private var offset: Int) : Runnable {
        private var realTotalOffset: Int = Integer.MAX_VALUE
        private var realOffset: Int = 0

        override fun run() {
            if (realTotalOffset == Integer.MAX_VALUE) {
                realTotalOffset = offset
            }
            realOffset = (realTotalOffset.toFloat() * 0.1f).toInt()

            if (realOffset == 0) {
                realOffset = if (realTotalOffset < 0) -1 else 1

            }
            if (abs(realTotalOffset) <= 0) {
                cancelFuture()
                handler.sendEmptyMessage(LoopHandler.WHAT_ITEM_SELECTED)
            } else {
                totalScrollY += realOffset
                postInvalidate()
                realTotalOffset -= realOffset
            }
        }
    }

    inner class InertiaTimerTask(private var velocityY: Int) : Runnable {
        private val TAG = "InertiaTimerTask"
        private var realVelocityY = Integer.MAX_VALUE.toFloat()

        init {
            Log.i(TAG, "InertiaTimerTask----init--->")
        }

        override fun run() {
            Log.i(TAG, "velocityY--->$velocityY")
            Log.i(TAG, "realVelocityY--->$realVelocityY")
            if (realVelocityY == Integer.MAX_VALUE.toFloat()) {
                realVelocityY = if (abs(velocityY) > 2000f) {
                    if (velocityY > 0.0f) {
                        2000f
                    } else {
                        -2000f
                    }
                } else velocityY.toFloat()
            }
            if (abs(realVelocityY) in 0.0f..20f) {
                Log.i(TAG, "WHAT_SMOOTH_SCROLL_INERTIA--->")
                handler.sendEmptyMessageDelayed(LoopHandler.WHAT_SMOOTH_SCROLL_INERTIA, 60)
                cancelFuture()
                handler.sendEmptyMessage(LoopHandler.WHAT_SMOOTH_SCROLL)
                return
            }
            val i = (realVelocityY * 10f / 1000f).toInt()
            totalScrollY -= i
            if (!isLoop) {
                val itemHeight = itemTextHeight
                if (totalScrollY <= ((-initPosition).toFloat() * itemHeight).toInt()) {
                    realVelocityY = 40f
                    totalScrollY = ((-initPosition).toFloat() * itemHeight).toInt()
                } else if (totalScrollY >= ((items.size - 1 - initPosition).toFloat() * itemHeight).toInt()) {
                    totalScrollY = ((items.size - 1 - initPosition).toFloat() * itemHeight).toInt()
                    realVelocityY = -40f
                }
            }
            if (realVelocityY < 0.0f) {
                realVelocityY += 20f
            } else {
                realVelocityY -= 20f
            }
            postInvalidate()
        }
    }

    interface OnItemScrollListener {
        /**
         * 滚轮滚动状态变化监听
         * @param loopView
         * @param currentPassItem 当前经过的item
         * @param oldScrollState  上一次滚动状态
         * @param scrollState  当前滚动状态
         * @param totalScrollY 滚动距离
         */
        fun onItemScrollStateChanged(
            loopView: WheelView,
            currentPassItem: Int,
            oldScrollState: Int,
            scrollState: Int,
            totalScrollY: Int
        )

        /***
         * 滚轮滚动监听
         * @param loopView
         * @param currentPassItem 当前经过的item
         * @param scrollState 当前滚动状态
         * @param totalScrollY 滚动距离
         */
        fun onItemScrolling(
            loopView: WheelView,
            currentPassItem: Int,
            scrollState: Int,
            totalScrollY: Int
        )
    }

    inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        //用户按下触摸屏、快速移动后松开
        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            cancelFuture()
            Log.i("WheelView", "onFling--------->")
            mFuture = mExecutor.scheduleWithFixedDelay(
                InertiaTimerTask(velocityY.toInt()), 0, 10L,
                TimeUnit.MILLISECONDS
            )
            changeScrollState(SCROLL_STATE_DRAGGING)
            return true
        }
    }

    interface OnItemSelectedListener {
        fun onItemSelected(index: Int)
    }


}