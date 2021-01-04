package com.example.baseframework.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.example.baseframework.R
import com.example.baseframework.ex.dip2px
import com.example.baseframework.ex.getResourcesColor
import kotlin.math.*

/**
 * 圆环拖动View
 */
class AnnulusView : View {
    private val mBgAnnulusPaint = Paint()
    private val mTestPaint = Paint()
    private val mBtnPaint = Paint()
    private val bgAnnulusHeadPaint = Paint()
    private val mProgressPaint = Paint()

    //按钮半径
    private val defaultPointRadius: Int =context.dip2px(16)

    //背景圆环宽度
    private val annulusWidth: Int = context.dip2px(8)

    private var annulusRadius = 0
    private var btnRadius = 0
    private var onClickBtnRadius=0
    private val tag = "AnnulusView"
    private val isDebug = true

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
    private fun initTypedArray(attrs: AttributeSet?) {

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.AnnulusView)
            mBgAnnulusPaint.color = typedArray.getInteger(R.styleable.AnnulusView_annulus_color,context.getResourcesColor(R.color.colorPrimary))
            bgAnnulusHeadPaint.color = mBgAnnulusPaint.color
            mProgressPaint.color =  typedArray.getInteger(R.styleable.AnnulusView_annulus_selected_color,context.getResourcesColor(R.color.colorAccent))
            mBtnPaint.color = mProgressPaint.color
            val angle=typedArray.getInteger(R.styleable.AnnulusView_progress,0)
            degreesAngle += ((maxDegrees - minDegrees) * (angle / 100f)).toInt()
            isClick = typedArray.getBoolean(R.styleable.AnnulusView_btn_isClick,true)
            val bitmapResId = typedArray.getInteger(R.styleable.AnnulusView_btn_bitmap,0)
            if(bitmapResId>0){
                val bitmap = BitmapFactory.decodeResource(resources,bitmapResId)
                btnBitmap = bitmap
            }
        typedArray.recycle()
    }

    init {
        mBgAnnulusPaint.isAntiAlias = true
        mBgAnnulusPaint.style = Paint.Style.STROKE
        mBgAnnulusPaint.color = context.getResourcesColor(R.color.colorPrimary)
        mBgAnnulusPaint.strokeWidth = annulusWidth.toFloat()

        bgAnnulusHeadPaint.isAntiAlias = true
        bgAnnulusHeadPaint.style = Paint.Style.FILL
        bgAnnulusHeadPaint.color = mBgAnnulusPaint.color




        mProgressPaint.isAntiAlias = true
        mProgressPaint.style = Paint.Style.STROKE
        mProgressPaint.color = context.getResourcesColor(R.color.colorAccent)
        mProgressPaint.strokeWidth = annulusWidth.toFloat()

        mBtnPaint.isAntiAlias = true
        mBtnPaint.style = Paint.Style.FILL
        mBtnPaint.color = mProgressPaint.color
    }


    private var offsetX = 0f
    private var offsetY = 0f
    private val minDegrees = 135
    private val maxDegrees = 405
    private var rectF: RectF? = null

    @SuppressLint("DrawAllocation")
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        //根据宽高获取到圆环半径
        val viewWidth = MeasureSpec.getSize(widthMeasureSpec)
        val viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        offsetX = viewWidth / 2f
        offsetY = viewHeight / 2f
        annulusRadius = when {
            btnBitmap != null -> {
                btnRadius = btnBitmap!!.width / 2
                (min(viewWidth, viewHeight)) / 2 - btnBitmap!!.width / 2
            }
            else -> {
                btnRadius = defaultPointRadius
                if(isClick){
                    onClickBtnRadius =  (btnRadius * 1.125f).toInt()
                }
                (min(
                    viewWidth,
                    viewHeight
                )) / 2 - if (isClick) (btnRadius * 1.125F).toInt() else btnRadius
            }
        }


        val left = -annulusRadius
        val top = -annulusRadius
        val right = annulusRadius
        val bottom = annulusRadius
        rectF = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //首先将坐标系调整到View正中，以便于计算
        canvas.translate(offsetX, offsetY)
        //绘制背景圆环
        canvas.drawArc(
            rectF!!,
            minDegrees.toFloat(),
            (maxDegrees - minDegrees).toFloat(),
            false,
            mBgAnnulusPaint
        )
        if (isDebug) {
            canvas.drawLine(0f, -offsetY, 0f, offsetY, mTestPaint)
            canvas.drawLine(-offsetX, 0f, offsetX, 0f, mTestPaint)
        }
        //绘制背景圆环头
        canvas.save()
        canvas.rotate(45f, 0f, 0f)
        bgAnnulusHeadPaint.color = mBgAnnulusPaint.color
        canvas.drawCircle(
            (annulusRadius).toFloat(),
            0f,
            annulusWidth.toFloat() / 2,
            bgAnnulusHeadPaint
        )
        canvas.rotate(90f, 0f, 0f)
        bgAnnulusHeadPaint.color = mProgressPaint.color
        canvas.drawCircle(
            (annulusRadius).toFloat(),
            0f,
            annulusWidth.toFloat() / 2,
            bgAnnulusHeadPaint
        )
        canvas.restore()
        //绘制进度
        canvas.drawArc(
            rectF!!,
            minDegrees.toFloat(),
            degreesAngle - minDegrees.toFloat(),
            false,
            mProgressPaint
        )
        canvas.save()
        //变化范围 135-405之间
        canvas.rotate(degreesAngle.toFloat(), 0f, 0f)
        when{
            btnBitmap != null->{
                canvas.drawBitmap(
                    btnBitmap!!,
                    (annulusRadius - btnBitmap!!.width / 2).toFloat(),
                    (-btnBitmap!!.width / 2).toFloat(),
                    mBtnPaint
                )
            }
            else ->{
                canvas.drawCircle(
                    (annulusRadius).toFloat(),
                    0f,
                    btnRadius.toFloat(),
                    mBtnPaint
                )
            }
        }
        canvas.restore()
    }

    /**
     * 按百分比
     */
    fun setAngle(angle: Int) {
        when (angle) {
            in 0..100 -> {
                degreesAngle += ((maxDegrees - minDegrees) * (angle / 100f)).toInt()
                log("degreesAngle--->$degreesAngle")
                invalidate()
            }
        }
    }

    private var isClick = true
    fun isClickBtn(isClick: Boolean) {
        if (btnBitmap != null) {
            return
        }
        this.isClick = isClick
        invalidate()
    }

    private var btnBitmap: Bitmap? = null
    fun setBtnBitmap(bitmap: Bitmap) {
        btnBitmap = bitmap
        isClick = false
        invalidate()
    }

    private var listener: OnAngleChangeListener? = null
    fun addAngleChangeListener(listener: OnAngleChangeListener?) {
        this.listener = listener
    }

    interface OnAngleChangeListener {
        fun onAngleChange(change: Int)
    }

    private var degreesAngle: Int = minDegrees
        set(value) =
            when {
                value > maxDegrees -> {
                    field = maxDegrees
                }
                value < minDegrees -> {
                    field = minDegrees
                }
                else -> {
                    field = value
                }
            }


    private var isTouchBtnRegion = false
    private var isShiftOutBtnRegion = true

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isTouchBtnRegion = isTouchBtn(event)
                if (isTouchBtnRegion) {
                    isShiftOutBtnRegion = false
                    if (isClick) {
                        btnRadius = onClickBtnRadius
                    }
                    invalidate()
                }
                log("isTouchPoint--->$isTouchBtnRegion")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouchBtnRegion) {
                    degreesAngle = targetAngle(event)
                    if (!isShiftOutBtnRegion) {
                        log("degreesAngle--->$degreesAngle")
                        invalidate()
                        listener?.onAngleChange((((degreesAngle - minDegrees) / (maxDegrees - minDegrees.toFloat())) * 100).toInt())
                    }
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isTouchBtnRegion&&isClick) {
                    btnRadius = defaultPointRadius
                }
                isTouchBtnRegion = false
                isShiftOutBtnRegion = true

                invalidate()
                return true
            }
            else -> {
                isTouchBtnRegion = false
            }
        }
        return super.onTouchEvent(event)
    }


    private fun targetAngle(event: MotionEvent): Int {
        //首先进行点向坐标系偏移
        val realX = event.x - offsetX
        val realY = event.y - offsetY
        var targetAngle = degreesAngle
        val ru = annulusRadius
        val minRu = ru - btnRadius * 3
        val maxRu = ru + btnRadius * 2
        val r = sqrt(realX * realX + realY * realY)
        val a = Math.toDegrees(asin(realX / r).toDouble())
        log("a--->$a")
        if (r >= minRu && r <= maxRu) {
            //在圆环范围内
            if (!isShiftOutBtnRegion) {
                targetAngle = getTargetAngle(realX, realY, a)
            } else {
                //如果滑出按钮区域就判断当前点是否在btn区域
                isShiftOutBtnRegion = !isTouchBtn(event)
                if (!isShiftOutBtnRegion) {
                    targetAngle = getTargetAngle(realX, realY, a)
                }
            }
        } else {
            isShiftOutBtnRegion = true
        }
        return targetAngle
    }

    private fun getTargetAngle(realX: Float, realY: Float, a: Double): Int {
        var targetAngle = degreesAngle
        if (realX > 0 && realY > 0) {
            //第一象限
            targetAngle = 360
            val angle = 90 - abs(a.toInt())
            log("angle--->$angle")
            targetAngle += angle
        } else if (realX < 0 && realY > 0) {
            targetAngle = minDegrees
            //第二象限
            val angle = abs(a.toInt()) - 45
            log("angle--->$angle")
            targetAngle += angle
        } else if (realX < 0 && realY < 0) {
            //第三象限
            targetAngle = 180
            val angle = 90 - abs(a.toInt())
            log("angle--->$angle")
            targetAngle += angle
        } else if (realX > 0 && realY < 0) {
            //第四象限
            targetAngle = 270
            val angle = abs(a.toInt())
            log("angle--->$angle")
            targetAngle += angle
        }
        return targetAngle
    }

    /**
     * 判断触摸点是否落在btn上
     */
    private fun isTouchBtn(event: MotionEvent): Boolean {
        val realX = event.x - offsetX
        val realY = event.y - offsetY
        log("x--->$realX ,y---->$realY")
        if (degreesAngle in minDegrees..maxDegrees) {
            val pointX =
                cos(Math.toRadians(degreesAngle.toDouble())) * (annulusRadius)
            val pointY =
                sin(Math.toRadians(degreesAngle.toDouble())) * (annulusRadius)
            val minX = pointX - btnRadius
            val minY = pointY - btnRadius

            val maxX = pointX + btnRadius
            val maxY = pointY + btnRadius
            if (realX in minX..maxX && realY in minY..maxY) {
                return true
            }
        }
        return false
    }

    private fun log(text: String) {
        if (isDebug) {
            Log.i(tag, text)
        }
    }

}