package com.xiedi.testapp.test.broken.animator

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import java.util.*
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.round

object Utils {
    var screenWidth = 0
    var screenHeight = 0
    private val random = Random()
    private val DENSITY = Resources.getSystem().displayMetrics.density
    private val mCanvas = Canvas()

    fun dp2px(dp: Int) = round(dp * DENSITY)

    fun convertViewToBitmap(view: View): Bitmap? {
        val bitmap = createBitmapSafely(
            view.width,
            view.height,
            2
        )
        if (bitmap != null) {
            mCanvas.setBitmap(bitmap)
            mCanvas.translate(-view.scrollX.toFloat(), -view.scrollY.toFloat())
            view.draw(mCanvas)
            mCanvas.setBitmap(null)
        }
        return bitmap
    }

    fun createBitmapSafely(width: Int, height: Int, retryCount: Int): Bitmap? {
        var retry = retryCount
        while (retry-- > 0) {
            try {
                return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
                System.gc()
            }
        }
        return null
    }

    fun nextInt(a: Int, b: Int): Int=min(a, b) + random.nextInt(abs(a - b))

    fun nextInt(a: Int): Int = random.nextInt(a)

    fun nextFloat(a: Float, b: Float): Float = min(a, b) + random.nextFloat() * abs(a - b)

    fun nextFloat(a: Float): Float =random.nextFloat() * a

    fun nextBoolean(): Boolean = random.nextBoolean()



}