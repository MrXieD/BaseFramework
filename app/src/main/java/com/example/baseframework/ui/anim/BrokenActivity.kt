package com.example.baseframework.ui.anim

import android.annotation.SuppressLint
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import com.example.baseframework.activity.BaseVBActivity
import com.example.baseframework.databinding.ActivityBrokenBinding
import com.example.baseframework.ui.anim.broken.BrokenCallback
import com.example.baseframework.ui.anim.broken.BrokenTouchListener
import com.example.baseframework.ui.anim.broken.ShakeListener
import com.example.baseframework.ui.anim.broken.Utils
import com.example.baseframework.log.XLog

class BrokenActivity : BaseVBActivity<ActivityBrokenBinding>() {
    private var shakeListener: ShakeListener? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewContainer.btnShow.setOnClickListener {
            mViewContainer.ivMain.visibility = View.VISIBLE
            shakeListener?.start()
        }
        mViewContainer.bvTest.setCallback(object :
            BrokenCallback {
            override fun onStart(v: View) {
                XLog.i("BrokenActivity", "onStart--->")
            }

            override fun onCancel(v: View) {
                XLog.i("BrokenActivity", "onCancel--->")
            }

            override fun onRestart(v: View) {
                XLog.i("BrokenActivity", "onRestart--->")
            }

            override fun onFalling(v: View) {
                XLog.i("BrokenActivity", "onFalling--->")
            }

            override fun onFallingEnd(v: View) {
                XLog.i("BrokenActivity", "onFallingEnd--->")
            }

            override fun onCancelEnd(v: View) {
                XLog.i("BrokenActivity", "onCancelEnd--->")
            }
        })
        // 将你要使用特效的控件,设置点击事件
        val brokenTouchListener = BrokenTouchListener(mViewContainer.bvTest, mViewContainer.ivMain)
        mViewContainer.ivMain.setOnTouchListener(brokenTouchListener)
        shakeListener = ShakeListener(this)
        shakeListener?.setShakeListener {
            runOnUiThread {
                if(mViewContainer.ivMain.visibility ==View.VISIBLE) {
                    val rect1 = Rect()
                    mViewContainer.ivMain.getGlobalVisibleRect(rect1)
                    val randomY = Utils.nextInt(rect1.top, rect1.bottom)
                    brokenTouchListener.performClick(
                        Point(if (it) mViewContainer.ivMain.left else mViewContainer.ivMain.right, randomY)
                    )
                    shakeListener?.stop(false)
                }
            }
        }
        shakeListener?.start()
    }


    override fun onDestroy() {
        super.onDestroy()
        shakeListener?.stop(true)
    }
}