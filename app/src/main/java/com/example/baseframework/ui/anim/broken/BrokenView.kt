package com.example.baseframework.ui.anim.broken

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import java.util.*
import kotlin.collections.HashMap

/**
 * 玻璃破碎效果View
 */
class BrokenView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var enable = true
    private var callBack: BrokenCallback? = null
    private val animMap: HashMap<View, BrokenAnimator> = HashMap()
    private val animList: LinkedList<BrokenAnimator> = LinkedList()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val viewGroup = parent as ViewGroup
        val view = viewGroup.getChildAt(0)
        Utils.screenHeight = view.measuredHeight
        setMeasuredDimension(view.measuredWidth, view.measuredHeight)
    }

    override fun onDraw(canvas: Canvas) {
        if (animList.isEmpty()) return
        val iterator: ListIterator<BrokenAnimator> = animList.listIterator(animList.size)
        while (iterator.hasPrevious()) {
            iterator.previous().draw(canvas)
        }
    }


    fun getAnimator(view: View?): BrokenAnimator? {
        val bAnim: BrokenAnimator? = animMap[view]
        return if (bAnim != null && bAnim.getStage() != BrokenAnimator.STAGE_EARLY_END) bAnim else null
    }

    fun createAnimator(view: View, point: Point, config: BrokenConfig): BrokenAnimator? {
        Log.i("BrokenView", "createAnimator--->")
        val bitmap = Utils.convertViewToBitmap(view) ?: return null
        val bAnim = BrokenAnimator(this, view, bitmap, point, config)
        bAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                val anim = animation as BrokenAnimator
                Log.i("BrokenView", "onAnimationEnd--->" + anim.animatedFraction)
                animMap.remove(view)
                animList.remove(anim)
                if (anim.getStage() == BrokenAnimator.STAGE_BREAKING) {
                    onBrokenCancelEnd(view)
                } else if (anim.getStage() == BrokenAnimator.STAGE_FALLING) {
                    onBrokenFallingEnd(view)
                }
            }

            override fun onAnimationRepeat(animation: Animator?) {
                val anim = animation as BrokenAnimator
                anim.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                    override fun onAnimationUpdate(animation: ValueAnimator) {
                        val value = animation.animatedValue as Float
                        if (value <= 0.8f) {
                            anim.setStage(BrokenAnimator.STAGE_FALLING)
                            onBrokenFalling(view)
                            anim.removeUpdateListener(this)
                        }
                    }

                })
            }
        })
        bAnim.addUpdateListener {
            invalidate()
        }
        animList.addLast(bAnim)
        animMap[view] = bAnim
        return bAnim
    }

    fun add2Window(activity: Activity): BrokenView {
        val rootView = activity.findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val brokenView = BrokenView(activity)
        rootView.addView(
            brokenView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        Utils.screenWidth = dm.widthPixels
        Utils.screenHeight = dm.heightPixels
        return brokenView
    }

    fun reset() {
        val iterator: ListIterator<BrokenAnimator> = animList.listIterator()
        while (iterator.hasNext()) {
            val bAnim = iterator.next()
            bAnim.removeAllListeners()
            bAnim.cancel()
        }
        animList.clear()
        animMap.clear()
        invalidate()
    }

    fun setCallback(c: BrokenCallback) {
        callBack = c
    }

    fun onBrokenCancel(v: View) {
        callBack?.onCancel(v)
    }

    fun onBrokenStart(v: View) {
        callBack?.onStart(v)
    }

    fun onBrokenCancelEnd(v: View) {
        callBack?.onCancelEnd(v)
    }

    fun onBrokenFallingEnd(v: View) {
        callBack?.onFallingEnd(v)
    }

    fun onBrokenRestart(v: View) {
        callBack?.onRestart(v)
    }

    fun onBrokenFalling(v: View) {
        v.visibility = INVISIBLE
        callBack?.onFalling(v)
    }


}