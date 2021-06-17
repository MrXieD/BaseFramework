package com.example.draw

import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.PixelCopy
import android.view.View
import android.view.Window
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream

/**
@author Anthony.H
@date: 2021/6/17
@desription:
 */
class MyCanvasView(context: Context?, private val window: Window) : View(context) {


    private lateinit var bitMap: Bitmap
    private lateinit var myCanvas: Canvas
    private var paint: Paint = Paint()

    init {
        paint.color = Color.YELLOW
        isDrawingCacheEnabled = true
        buildDrawingCache()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        bitMap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        saveBitmap(bitMap, "pic1")
        myCanvas = Canvas(bitMap)
        myCanvas.drawColor(Color.BLUE)
        //导出的图为蓝色，说明成功将内容绘制到了bitMap上
        saveBitmap(bitMap, "pic2")
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.let {
//            it.drawBitmap(bitMap, 0f, 0f, paint)
            it.drawColor(Color.BLUE)
            it.drawCircle(width / 2f, height / 2f, width / 2f, paint)
            //导出的图为蓝色，没有圆，说明圆的绘制和bitMap无关。
            saveBitmap(bitMap, "pic3")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getViewOwnBitmap(): Bitmap {
        val locationArray = IntArray(2)
        getLocationInWindow(locationArray)
        val bitMap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        PixelCopy.request(
            window, Rect(
                locationArray[0], locationArray[1], locationArray[0] + width,
                locationArray[1] + height
            ), bitMap, {
                Log.e("PixelCopy", "getViewOwnBitmap:copy finished:$it")
            }, Handler(Looper.getMainLooper())
        )
        return bitMap
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val real = width.coerceAtMost(height)
        setMeasuredDimension(real, real)
    }


    /**
     * 生成的图片见assets中的三张图片
     */
    private fun saveBitmap(bitMap: Bitmap, fileName: String) {
//        val path = "/sdcard/${fileName}"
        val path = "${context.applicationContext.cacheDir}${File.separator}${fileName}.jpg"
        val file = File(path)
        if (!file.exists()) {
            file.createNewFile()
        }
        val fileOs = FileOutputStream(file)
        bitMap.compress(Bitmap.CompressFormat.JPEG, 100, fileOs)
        fileOs.flush()
        fileOs.close()
        Log.e("saveBitmap", "saveBitmap: finish")
    }

}