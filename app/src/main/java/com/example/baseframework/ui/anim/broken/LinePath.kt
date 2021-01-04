package com.example.baseframework.ui.anim.broken

import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import kotlin.math.tan

class LinePath() : Path(){
    private var startLength = -1f
    val points:ArrayList<Point> = ArrayList()
    private var endPoint: Point = Point()
    private var straight = false

    fun setEndPoint(endPoint: Point) {
        this.endPoint = endPoint
    }

    fun setEndPoint(endX: Int, endY: Int) {
        endPoint[endX] = endY
    }

    fun getEndPoint(): Point = endPoint

    fun getEndX(): Int = endPoint.x

    fun getEndY(): Int = endPoint.y

    fun isStraight(): Boolean =straight

    fun setStraight(straight: Boolean) {
        this.straight = straight
    }

    fun getStartLength(): Float {
        return startLength
    }

    fun setStartLength(startLength: Float) {
        this.startLength = startLength
    }

    fun lineToEnd() {
        lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())
    }

    fun obtainEndPoint(angleRandom: Int, angleBase: IntArray, r: Rect) {
        val gradient = -tan(Math.toRadians(angleRandom.toDouble())).toFloat()
        var endX = 0
        var endY = 0
        when (angleRandom) {
            in 0..89 -> {
                when {
                    angleRandom < angleBase[0] -> {
                        endX = r.right
                        endY = (endX * gradient).toInt()
                    }
                    angleRandom > angleBase[0] -> {
                        endY = r.top
                        endX = (endY / gradient).toInt()
                    }
                    angleRandom == angleBase[0] -> {
                        endY = r.top
                        endX = r.right
                    }
                }
            }
            in 91..180 -> {
                when {
                    180 - angleRandom < angleBase[1] -> {
                        endX = r.left
                        endY = (endX * gradient).toInt()
                    }
                    180 - angleRandom > angleBase[1] -> {
                        endY = r.top
                        endX = (endY / gradient).toInt()
                    }
                    180 - angleRandom == angleBase[1] -> {
                        endY = r.top
                        endX = r.left
                    }
                }
            }
            in 181..269 -> {
                when {
                    angleRandom - 180 < angleBase[2] -> {
                        endX = r.left
                        endY = (endX * gradient).toInt()
                    }
                    angleRandom - 180 > angleBase[2] -> {
                        endY = r.bottom
                        endX = (endY / gradient).toInt()
                    }
                    angleRandom - 180 == angleBase[2] -> {
                        endY = r.bottom
                        endX = r.left
                    }
                }
            }
            in 271..359 -> {
                when {
                    360 - angleRandom < angleBase[3] -> {
                        endX = r.right
                        endY = (endX * gradient).toInt()
                    }
                    360 - angleRandom > angleBase[3] -> {
                        endY = r.bottom
                        endX = (endY / gradient).toInt()
                    }
                    360 - angleRandom == angleBase[3] -> {
                        endY = r.bottom
                        endX = r.right
                    }
                }
            }
            90 -> {
                endX = 0
                endY = r.top
            }
            270 -> {
                endX = 0
                endY = r.bottom
            }
        }
        endPoint[endX] = endY
    }
}