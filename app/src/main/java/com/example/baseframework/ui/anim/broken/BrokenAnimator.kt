package com.example.baseframework.ui.anim.broken

import android.animation.ValueAnimator
import android.graphics.*
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.atan

class BrokenAnimator(private val mBrokenView:BrokenView,private val mView: View,private val mBitmap: Bitmap,private val mTouchPoint: Point,private val mConfig:BrokenConfig) : ValueAnimator() {

    companion object{
        const val STAGE_BREAKING = 1
        const val STAGE_FALLING = 2
        const val STAGE_EARLY_END = 3
        const val DEFAULT_RIFTS_RADIUS = 66
    }

    private var stage = STAGE_BREAKING

    private val onDrawPaint: Paint = Paint()
    private val onDrawPath: Path = Path()
    private val onDrawPM: PathMeasure = PathMeasure()

    private var hasCircleRifts = true

    private var lineRifts: Array<LinePath> = Array(mConfig.complexity){LinePath()}
    private var circleRifts: Array<Path?> = arrayOfNulls(mConfig.complexity)
    private var circleWidth: IntArray = IntArray(mConfig.complexity)
    private val pathArray: ArrayList<Path> = ArrayList()
    private var pieces: Array<Piece?>? = null

    private var offsetX = 0
    private var offsetY = 0
    private var SEGMENT = 0

    init {
        SEGMENT = mConfig.circleRiftsRadius
        if (SEGMENT == 0) {
            hasCircleRifts = false
            SEGMENT = 66
        }
        val r = Rect()
        mView.getGlobalVisibleRect(r)
        offsetX = mTouchPoint.x - r.left
        offsetY = mTouchPoint.y - r.top
        // Make the touchPoint be the origin of coordinates
        // Make the touchPoint be the origin of coordinates
        r.offset(-mTouchPoint.x, -mTouchPoint.y)

        // The touchPoint is original location on the screen,
        // but the BrokenView canvas may be not full screen (under status bar),
        // to do this we can translate the canvas correctly.
        val brokenViewR = Rect()
        mBrokenView.getGlobalVisibleRect(brokenViewR)
        mTouchPoint.x -= brokenViewR.left
        mTouchPoint.y -= brokenViewR.top

        buildBrokenLines(r)
        buildBrokenAreas(r)
        buildPieces()
        buildPaintShader()
        warpStraightLines()
        repeatCount = 1
        setFloatValues(0f, 1f)
        interpolator = LinearInterpolator()
        duration = mConfig.breakDuration

    }

    private fun warpStraightLines() {
        val pmTemp = PathMeasure()
        for (i in 0 until mConfig.complexity) {
            if (lineRifts[i].isStraight()) {
                pmTemp.setPath(lineRifts[i], false)
                lineRifts[i].setStartLength(pmTemp.length / 2)
                val pos = FloatArray(2)
                pmTemp.getPosTan(pmTemp.length / 2, pos, null)
                val xRandom = (pos[0] + Utils.nextInt(
                        -Utils.dp2px(1).toInt(),
                        Utils.dp2px(1).toInt()
                    )).toInt()
                val yRandom = (pos[1] + Utils.nextInt(
                        -Utils.dp2px(1).toInt(),
                        Utils.dp2px(1).toInt()
                    )).toInt()
                lineRifts[i].reset()
                lineRifts[i].moveTo(0f, 0f)
                lineRifts[i].lineTo(xRandom.toFloat(), yRandom.toFloat())
                lineRifts[i].lineToEnd()
            }
        }
    }

    private fun buildPaintShader() {
        val shader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        val matrix = Matrix()
        // Refraction effect
        matrix.setTranslate(-offsetX - 10.toFloat(), -offsetY - 7.toFloat())
        shader.setLocalMatrix(matrix)
        val cMatrix = ColorMatrix()
        // Increase saturation and brightness
        cMatrix.set(floatArrayOf(
                2.5f, 0f, 0f, 0f, 100f,
                0f, 2.5f, 0f, 0f, 100f,
                0f, 0f, 2.5f, 0f, 100f,
                0f, 0f, 0f, 1f, 0f))
        onDrawPaint.colorFilter = ColorMatrixColorFilter(cMatrix)
        onDrawPaint.shader = shader
        onDrawPaint.style = Paint.Style.FILL
    }

    private fun buildPieces() {
        pieces = arrayOfNulls(pathArray.size)
        val paint = Paint()
        val matrix = Matrix()
        val canvas = Canvas()
        for (i  in pieces!!.indices){
            val shadow = Utils.nextInt(Utils.dp2px(2).toInt(), Utils.dp2px(9).toInt())
            val path = pathArray[i]
            val r = RectF()
            path.computeBounds(r, true)
            val pBitmap = Utils.createBitmapSafely(
                r.width().toInt() + shadow * 2,
                r.height().toInt() + shadow * 2, 1
            )
            if (pBitmap == null) {
                pieces!![i] = Piece(-1, -1, null, shadow)
                continue
            }
            pieces!![i] = Piece(
                r.left.toInt() + mTouchPoint.x - shadow,
                r.top.toInt() + mTouchPoint.y - shadow, pBitmap, shadow
            )
            canvas.setBitmap(pieces!![i]!!.bitmap)
            val mBitmapShader = BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
            matrix.reset()
            matrix.setTranslate(-r.left - offsetX + shadow, -r.top - offsetY + shadow)
            mBitmapShader.setLocalMatrix(matrix)
            paint.reset()
            val offsetPath = Path()
            offsetPath.addPath(path, -r.left + shadow, -r.top + shadow)
            // Draw shadow
            paint.style = Paint.Style.FILL
            paint.setShadowLayer(shadow.toFloat(), 0f, 0f,0xff333333.toInt())
            canvas.drawPath(offsetPath, paint)
            paint.setShadowLayer(0f, 0f, 0f, 0)
            // In case the view has alpha channel
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.XOR)
            canvas.drawPath(offsetPath, paint)
            paint.xfermode = null
            // Draw bitmap
            paint.shader = mBitmapShader
            paint.alpha = 0xcc
            canvas.drawPath(offsetPath, paint)
        }
        // Sort by shadow
        Arrays.sort(pieces)
    }

    private fun buildBrokenAreas(r: Rect) {
        val SEGMENT_LESS = SEGMENT * 7 / 9
        val START_LENGTH = (SEGMENT * 1.1).toInt()
        // The Circle-Rifts is just some isosceles triangles,
        // "linkLen" is the length of oblique side
        var linkLen = 0f
        var repeat = 0

        val pmNow = PathMeasure()
        val pmPre = PathMeasure()
        
        for (i in 0 until mConfig.complexity){
            lineRifts[i].setStartLength(Utils.dp2px(START_LENGTH))
            if (repeat > 0) {
                repeat--
            } else {
                linkLen = Utils.nextInt(Utils.dp2px(SEGMENT_LESS).toInt(), Utils.dp2px(SEGMENT).toInt()).toFloat()
                repeat = Utils.nextInt(3)
            }
            val iPre = if (i - 1 < 0) mConfig.complexity - 1 else i - 1
            pmNow.setPath(lineRifts[i], false)
            pmPre.setPath(lineRifts[iPre], false)
            if (hasCircleRifts && pmNow.length > linkLen && pmPre.length > linkLen) {
                val pointNow = FloatArray(2)
                val pointPre = FloatArray(2)
                circleWidth[i] = Utils.nextInt(Utils.dp2px(1).toInt()) + 1
                circleRifts[i] = Path()
                pmNow.getPosTan(linkLen, pointNow, null)
                circleRifts[i]!!.moveTo(pointNow[0], pointNow[1])
                pmPre.getPosTan(linkLen, pointPre, null)
                circleRifts[i]!!.lineTo(pointPre[0], pointPre[1])

                // The area outside Circle-Rifts

                // The area outside Circle-Rifts
                var pathArea = Path()
                pmPre.getSegment(linkLen, pmPre.length, pathArea, true)
                pathArea.rLineTo(0f, 0f) // KITKAT(API 19) and earlier need it

                drawBorder(
                    pathArea, lineRifts[iPre].getEndPoint(),
                    lineRifts[i].points[lineRifts[i].points.size - 1], r
                )
                for (j in lineRifts[i].points.size - 2 downTo 0) {
                    pathArea.lineTo(lineRifts[i].points[j].x.toFloat(), lineRifts[i].points[j].y.toFloat())
                }
                pathArea.lineTo(pointNow[0], pointNow[1])
                pathArea.lineTo(pointPre[0], pointPre[1])
                pathArea.close()
                pathArray.add(pathArea)


                // The area inside Circle-Rifts, it's a isosceles triangles
                pathArea = Path()
                pathArea.moveTo(0f, 0f)
                pathArea.lineTo(pointPre[0], pointPre[1])
                pathArea.lineTo(pointNow[0], pointNow[1])
                pathArea.close()
                pathArray.add(pathArea)
            }else{
                // Too short, there is no Circle-Rifts
                val pathArea = Path(lineRifts[iPre])
                drawBorder(pathArea, lineRifts[iPre].getEndPoint(), lineRifts[i].points[lineRifts[i].points.size - 1], r)
                for (j in lineRifts[i].points.size - 2 downTo 0){
                    pathArea.lineTo(lineRifts[i].points[j].x.toFloat(), lineRifts[i].points[j].y.toFloat())
                }
                pathArea.close()
                pathArray.add(pathArea)
            }
        }
    }

    private fun buildBrokenLines(r: Rect) {
        val baseLines = Array(mConfig.complexity) {LinePath()}
        buildBaselines(baseLines, r)
        val pmTemp = PathMeasure()
        for (i in 0 until mConfig.complexity){
            lineRifts[i].moveTo(0f, 0f)
            lineRifts[i].setEndPoint(baseLines[i].getEndPoint())
            pmTemp.setPath(baseLines[i], false)
            val length = pmTemp.length
            val THRESHOLD = SEGMENT + SEGMENT / 2
            if (length > Utils.dp2px(THRESHOLD)) {
                lineRifts[i].setStraight(false)
                // First, line to the point at SEGMENT of baseline;
                // Second, line to the random-point at (SEGMENT+SEGMENT/2) of baseline;
                // So when we set the start-draw-length to SEGMENT and the paint style is "FILL",
                // we can make the line become visible faster(exactly, the triangle)
                val pos = FloatArray(2)
                pmTemp.getPosTan(Utils.dp2px(SEGMENT), pos, null)
                lineRifts[i].lineTo(pos[0], pos[1])
                lineRifts[i].points.add(Point(pos[0].toInt(), pos[1].toInt()))
                var xRandom: Int
                var yRandom: Int
                var step = Utils.dp2px(THRESHOLD)
                do {
                    pmTemp.getPosTan(step, pos, null)
                    // !!!
                    // Here determine the stroke width of lineRifts
                    xRandom = (pos[0] + Utils.nextInt(-Utils.dp2px(3).toInt(), Utils.dp2px(2).toInt())).toInt()
                    yRandom = (pos[1] + Utils.nextInt(-Utils.dp2px(2).toInt(), Utils.dp2px(3).toInt())).toInt()
                    lineRifts[i].lineTo(xRandom.toFloat(), yRandom.toFloat())
                    lineRifts[i].points.add(Point(xRandom, yRandom))
                    step += Utils.dp2px(SEGMENT)
                } while (step < length)
                lineRifts[i].lineToEnd()
            }else{
                // Too short, it's still a beeline, so we must warp it later {@warpStraightLines()},
                // to make sure it is visible in "FILL" mode.
                lineRifts[i] = baseLines[i]
                lineRifts[i].setStraight(true)
            }
            lineRifts[i].points.add(lineRifts[i].getEndPoint())
        }
    }

    private fun buildBaselines(baseLines: Array<LinePath>, r: Rect) {
        for (i in 0 until mConfig.complexity) {
            baseLines[i].moveTo(0f, 0f)
        }
        buildFirstLine(baseLines[0], r)

        // First angle
        var angle = Math.toDegrees(atan(-baseLines[0].getEndY() / baseLines[0].getEndX().toDouble())).toInt()

        // The four diagonal angle base
        val angleBase = IntArray(4)
        angleBase[0] = Math.toDegrees(atan((-r.top).toFloat() / r.right.toDouble())).toInt()
        angleBase[1] = Math.toDegrees(atan((-r.top).toFloat() / (-r.left).toDouble())).toInt()
        angleBase[2] = Math.toDegrees(atan(r.bottom.toFloat() / (-r.left).toDouble())).toInt()
        angleBase[3] = Math.toDegrees(atan(r.bottom.toFloat() / r.right.toDouble())).toInt()

        if (baseLines[0].getEndX() < 0) {
            // 2-quadrant,3-quadrant
            angle += 180
        } else if (baseLines[0].getEndX() > 0 && baseLines[0].getEndY() > 0) {
            // 4-quadrant
            angle += 360
        }
        // Random angle range
        val range = 360 / mConfig.complexity / 3
        var angleRandom: Int
        for (i in 1 until mConfig.complexity) {
            angle += 360 / mConfig.complexity
            if (angle >= 360) angle -= 360
            angleRandom = angle + Utils.nextInt(-range, range)
            if (angleRandom >= 360) {
                angleRandom -= 360
            } else if (angleRandom < 0) {
                angleRandom += 360
            }
            baseLines[i].obtainEndPoint(angleRandom, angleBase, r)
            baseLines[i].lineToEnd()
        }
    }

    private fun buildFirstLine(path: LinePath, r: Rect) {
        val range = intArrayOf(-r.left, -r.top, r.right, r.bottom)
        var max = -1
        var maxId = 0
        for (i in 0..3) {
            if (range[i] > max) {
                max = range[i]
                maxId = i
            }
        }
        when (maxId) {
            0 -> path.setEndPoint(
                r.left,
                Utils.nextInt(r.height()) + r.top
            )
            1 -> path.setEndPoint(
                Utils.nextInt(r.width()) + r.left,
                r.top
            )
            2 -> path.setEndPoint(
                r.right,
                Utils.nextInt(r.height()) + r.top
            )
            3 -> path.setEndPoint(
                Utils.nextInt(r.width()) + r.left,
                r.bottom
            )
        }
        path.lineToEnd()
    }

   private fun drawBorder(path: Path, pointStart: Point, pointEnd: Point, r: Rect) {
       when {
           pointStart.x == r.right -> {
               when {
                   pointEnd.x == r.right -> path.lineTo(
                       pointEnd.x.toFloat(),
                       pointEnd.y.toFloat()
                   )
                   pointEnd.y == r.top -> {
                       path.lineTo(r.right.toFloat(), r.top.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.x == r.left -> {
                       path.lineTo(r.right.toFloat(), r.top.toFloat())
                       path.lineTo(r.left.toFloat(), r.top.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.y == r.bottom -> {
                       path.lineTo(r.right.toFloat(), r.top.toFloat())
                       path.lineTo(r.left.toFloat(), r.top.toFloat())
                       path.lineTo(r.left.toFloat(), r.bottom.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
               }
           }
           pointStart.y == r.top -> {
               when {
                   pointEnd.y == r.top -> {
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.x == r.left -> {
                       path.lineTo(r.left.toFloat(), r.top.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.y == r.bottom -> {
                       path.lineTo(r.left.toFloat(), r.top.toFloat())
                       path.lineTo(r.left.toFloat(), r.bottom.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.x == r.right -> {
                       path.lineTo(r.left.toFloat(), r.top.toFloat())
                       path.lineTo(r.left.toFloat(), r.bottom.toFloat())
                       path.lineTo(r.right.toFloat(), r.bottom.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
               }
           }
           pointStart.x == r.left -> {
               when {
                   pointEnd.x == r.left -> {
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.y == r.bottom -> {
                       path.lineTo(r.left.toFloat(), r.bottom.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.x == r.right -> {
                       path.lineTo(r.left.toFloat(), r.bottom.toFloat())
                       path.lineTo(r.right.toFloat(), r.bottom.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.y == r.top -> {
                       path.lineTo(r.left.toFloat(), r.bottom.toFloat())
                       path.lineTo(r.right.toFloat(), r.bottom.toFloat())
                       path.lineTo(r.right.toFloat(), r.top.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
               }
           }
           pointStart.y == r.bottom -> {
               when {
                   pointEnd.y == r.bottom -> {
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.x == r.right -> {
                       path.lineTo(r.right.toFloat(), r.bottom.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.y == r.top -> {
                       path.lineTo(r.right.toFloat(), r.bottom.toFloat())
                       path.lineTo(r.right.toFloat(), r.top.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
                   pointEnd.x == r.left -> {
                       path.lineTo(r.right.toFloat(), r.bottom.toFloat())
                       path.lineTo(r.right.toFloat(), r.top.toFloat())
                       path.lineTo(r.left.toFloat(), r.top.toFloat())
                       path.lineTo(pointEnd.x.toFloat(), pointEnd.y.toFloat())
                   }
               }
           }
       }
    }


    fun setStage(s: Int) {
        stage = s
    }

    fun getStage(): Int = stage

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun draw(canvas: Canvas){
        if (!isStarted) {
            Log.i("BrokenAnimator", "Started--->")
        }
        val fraction = animatedValue as Float
        if (stage == STAGE_BREAKING){
            canvas.save()
            canvas.translate(mTouchPoint.x.toFloat(), mTouchPoint.y.toFloat())
            for (i in 0 until mConfig.complexity){
                onDrawPaint.style = Paint.Style.FILL
                onDrawPath.reset()
                onDrawPM.setPath(lineRifts[i], false)
                val pathLength = onDrawPM.length
                val startLength = lineRifts[i].getStartLength()
                var drawLength = startLength + fraction * (pathLength - startLength)
                if (drawLength > pathLength) drawLength = pathLength
                onDrawPM.getSegment(0f, drawLength, onDrawPath, false)
                onDrawPath.rLineTo(0f, 0f) // KITKAT(API 19) and earlier need it
                canvas.drawPath(onDrawPath, onDrawPaint)
                if (hasCircleRifts) {
                    if (circleRifts[i] != null && fraction > 0.1) {
                        onDrawPaint.style = Paint.Style.STROKE
                        var t = (fraction - 0.1f) * 2
                        if (t > 1) t = 1f
                        onDrawPaint.strokeWidth = circleWidth[i] * t
                        canvas.drawPath(circleRifts[i]!!, onDrawPaint)
                    }
                }
            }
            canvas.restore()
        }else if(stage == STAGE_FALLING){
            //Log.i("BrokenAnimator", "STAGE_FALLING---fraction--->" + fraction);
            var piecesNum = pieces!!.size
            for (p in pieces!!) {
                if (p?.bitmap != null && p.advance(fraction)){
                    canvas.drawBitmap(p.bitmap, p.matrix!!, null)
                } else {
                    piecesNum--
                }
            }
            if (piecesNum == 0) {
                stage=STAGE_EARLY_END
                mBrokenView.onBrokenFallingEnd(mView)
                cancel()
            }
        }
    }

}