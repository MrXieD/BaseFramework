package com.example.baseframework.ui.anim.broken

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.Point
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener

class BrokenTouchListener(val brokenView: BrokenView,val bindView: View) : OnTouchListener {
    private val gestureDetector: GestureDetector
    private val config = BrokenConfig()

    init {
        bindView.isClickable = true
        gestureDetector = GestureDetector(brokenView.context, BrokenGestureListener())
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean =
        brokenView.enable && gestureDetector.onTouchEvent(event)

    private var isSimulateClick = false

    fun performClick(point: Point) {
        Log.i(
            "BrokenTouchListener",
            "simulateClick--->" + Thread.currentThread()
                .name + " ,isSimulateClick--->" + isSimulateClick
        )
        if (isSimulateClick) {
            return
        }
        isSimulateClick = true
        var brokenAnim: BrokenAnimator? = brokenView.getAnimator(bindView)
        if (brokenAnim == null) {
            config.circleRiftsRadius = 0
            brokenAnim = brokenView.createAnimator(bindView, point, config)
        }
        if (brokenAnim == null) return
        brokenAnim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                isSimulateClick = false
            }

            override fun onAnimationCancel(animation: Animator) {
                super.onAnimationCancel(animation)
                isSimulateClick = false
            }
        })
        if (!brokenAnim.isStarted) {
            brokenAnim.start()
            brokenView.onBrokenStart(bindView)
        }
    }

    inner class BrokenGestureListener : SimpleOnGestureListener() {
        override fun onSingleTapUp(event: MotionEvent): Boolean {
            val point = Point(event.rawX.toInt(), event.rawY.toInt())
            var brokenAnim: BrokenAnimator? = brokenView.getAnimator(bindView)
            if (brokenAnim == null) config.circleRiftsRadius = BrokenAnimator.DEFAULT_RIFTS_RADIUS
            brokenAnim = brokenView.createAnimator(bindView, point, config)
            if (brokenAnim == null) return true
            if (!brokenAnim.isStarted) {
                brokenAnim.start()
                brokenView.onBrokenStart(bindView)
            }
            return true
        }
    }


}