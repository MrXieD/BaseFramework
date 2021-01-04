package com.example.baseframework.ui.anim.broken

import android.graphics.Bitmap
import android.graphics.Matrix

class Piece(private val x :Int,private val y:Int, val bitmap:Bitmap?, private val shadow:Int) : Comparable<Piece> {
    var matrix: Matrix? = null
    private var rotateX = 0
    private var rotateY = 0
    private var angle = 0f
    private var speed = 0f
    private var limitY = 0
    init {
        if (bitmap != null) {
            matrix = Matrix()
            matrix!!.postTranslate(x.toFloat(), y.toFloat())

            speed = Utils.nextFloat(1f, 4f)
            rotateX = Utils.nextInt(bitmap.width)
            rotateY = Utils.nextInt(bitmap.height)
            angle = Utils.nextFloat(0.3f) * if (Utils.nextBoolean()) 1 else -1

            val bitmapW = bitmap.width
            val bitmapH = bitmap.height
            limitY = if (bitmapW > bitmapH) bitmapW else bitmapH
            limitY += Utils.screenHeight
        }
    }


    override fun compareTo(other: Piece): Int = shadow - other.shadow

    fun advance(fraction: Float): Boolean {
        val s = (Math.pow(fraction * 1.1226, 2.0) * 8 * speed).toFloat()
        val zy = y + s * Utils.screenHeight / 10
        val r = fraction * fraction
        matrix?.reset()
        matrix?.setRotate(angle * r * 360, rotateX.toFloat(), rotateY.toFloat())
        matrix?.postTranslate(x.toFloat(), zy)
        return zy <= limitY
    }

}