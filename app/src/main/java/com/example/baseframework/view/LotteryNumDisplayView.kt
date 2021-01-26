package com.example.baseframework.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.baseframework.R
import com.example.baseframework.ex.dip2px
import com.example.baseframework.ex.getColorResource

/**
 * 彩票中奖号码展示View
 * 要求：
 * 1、可在XML中配置显示的可显示范围行数和列数以及总的行数和列数
 * 2、可水平和竖直惯性滑动，并且内容也做相应变化
 * 3、中奖号码内容配置
 * 4、中奖号码连线
 */
class LotteryNumDisplayView : View {
    //显示界面行数
    private var displayLineNum = 5
    //显示界面列数
    private var displayRowNum = 10

    private val mPaint:Paint = Paint()

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
        mPaint.style  = Paint.Style.STROKE
        mPaint.strokeWidth  = context.dip2px(1).toFloat()
    }
    private fun initTypedArray(attrs: AttributeSet?) {


    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //首先绘制网格

    }

}