package com.example.baseframework

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import com.example.baseframework.ui.ScreenAdaptHelper
import java.util.*

class BaseApplication : Application() {
    companion object Instance {
        var instance: BaseApplication? = null
        val baseInstance: BaseApplication by lazy { instance!! }
        val context: Context by lazy { baseInstance.applicationContext }
    }

    private val mActivityList = LinkedList<Activity>()
    override fun attachBaseContext(base: Context?) {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            override fun onActivityCreated(activity: Activity, p1: Bundle?) {
                mActivityList.add(activity)
            }

            override fun onActivityStarted(activity: Activity) {
            }

            override fun onActivityResumed(activity: Activity) {
            }

            override fun onActivityPaused(activity: Activity) {
            }

            override fun onActivityStopped(activity: Activity) {
            }

            override fun onActivityDestroyed(activity: Activity) {
                mActivityList.remove(activity)
            }

            override fun onActivitySaveInstanceState(activity: Activity, p1: Bundle) {
            }
        })
        //开启屏幕适配
        screenAdaptInit(360)
    }


    /**
     * 开启屏幕适配
     *
     * @param screenWidth 设计稿中屏幕的宽度。该宽度的单位必须和设计图中尺寸的标注单位一致。
     *                    比如设计图中的标注尺寸是dp， 那么这里也应该是dp；如果设计图中的标注尺寸是px，那么这里也应该是xp。
     *                    另外，APP中所有设计到尺寸的地方都应该和这里一致。
     */
    protected fun screenAdaptInit(screenWidth: Int) {
        ScreenAdaptHelper.screenAdaptInit(this, screenWidth)
    }

    /**
     * 检测指定Activity是打开了（包括Pause状态）
     */
    fun <T : Activity> activityIsOpened(activity: Class<T>): Boolean {
        return mActivityList.any { it::class.java == activity }
    }

    /**
     * finish所以activity
     */
    fun finishAllActivities(exitProcess: Boolean) {
        mActivityList.forEach { it.finish() }
        mActivityList.clear()
        if (exitProcess) {
            kotlin.system.exitProcess(0)
        }
    }



}