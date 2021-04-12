package com.example.baseframework.ui

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Canvas
import com.example.baseframework.BaseApplication
import com.example.baseframework.ex.px2dip
import com.example.baseframework.log.XLog

/**
 * Note: 屏幕适配方案
 *
 * dp最终都会转换成px。而：px = dp * density，density = DPI / 160 (160是android的一个标准值)
 * DPI(每英寸包含的像素个数) = 屏幕对角线像素个数 / 屏幕英寸值
 *
 * 该解决方案是修改density值，使得设置的dp值根据不同手机能计算出合适的px值（系统里是根据TypedValue.applyDimension()这个方法来转化的）
 */
@Suppress("unused", "MemberVisibilityCanBePrivate")
object ScreenAdaptHelper {
    private const val TAG = "ScreenAdapt"

    private var mScreenWidth = 0

    /**
     * 将适配后的像素值转换成适配前的像素值。
     *
     * 开启了屏幕适配后，获取到的控件的宽高就是适配后的值。
     * 如果此时使用了[Canvas]绘制控件，则需要将适配后的宽高转换成适配前的宽高（[Canvas]是使用的是原始宽高进行计算的）。
     *
     * @param context 适配之后的上下文
     * @param px      适配之后获取到的控件尺寸
     */
    fun convertOriginalPixel(context: Context, px: Float): Float {
        return context.px2dip(px) * Resources.getSystem().displayMetrics.density
    }

    /**
     * 将适配后的像素值转换成适配前的像素值。
     *
     * 开启了屏幕适配后，获取到的控件的宽高就是适配后的值。
     * 如果此时使用了[Canvas]绘制控件，则需要将适配后的宽高转换成适配前的宽高（[Canvas]是使用的是原始宽高进行计算的）。
     *
     * @param context 适配之后的上下文
     * @param px      适配之后获取到的控件尺寸
     */
    fun convertOriginalPixel(context: Context, px: Int): Int {
        return convertOriginalPixel(
            context,
            px.toFloat()
        ).toInt()
    }

    /**
     * 初始化该适配方案。
     */
    fun screenAdaptInit(app: Application, screenWidth: Int) {
        mScreenWidth = screenWidth
        enableScreenAdapt(app.resources)
    }

    fun enableScreenAdapt(res: Resources) {
        if (mScreenWidth <= 0) {
            return
        }

        val dm = res.displayMetrics
        val appDM = BaseApplication.baseInstance.resources.displayMetrics
        val sysDM = Resources.getSystem().displayMetrics

        XLog.d(TAG, "old density: ${dm.density}")
        XLog.d(TAG, "old scaledDensity: ${dm.scaledDensity}")
        XLog.d(TAG, "old densityDpi: ${dm.densityDpi}")

        // px = dp * density
        if (res.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            //是横屏还是竖屏
            dm.density = dm.widthPixels.toFloat() / mScreenWidth
        } else {
            dm.density = dm.heightPixels.toFloat() / mScreenWidth
        }

        dm.scaledDensity = dm.density * (sysDM.scaledDensity / sysDM.density)
        dm.densityDpi = (160 * dm.density).toInt()

        appDM.density = dm.density
        appDM.scaledDensity = dm.scaledDensity
        appDM.densityDpi = dm.densityDpi

        XLog.d(TAG, "new density: ${dm.density}")
        XLog.d(TAG, "new scaledDensity: ${dm.scaledDensity}")
        XLog.d(TAG, "new densityDpi: ${dm.densityDpi}")
    }

    fun disableScreenAdapt(res: Resources) {
        if (mScreenWidth <= 0) {
            return
        }

        val dm = res.displayMetrics
        val appDM = BaseApplication.baseInstance.resources.displayMetrics
        val sysDM = Resources.getSystem().displayMetrics

        XLog.d(TAG, "current density: ${dm.density}")
        XLog.d(TAG, "current scaledDensity: ${dm.scaledDensity}")
        XLog.d(TAG, "current densityDpi: ${dm.densityDpi}")

        dm.density = sysDM.density
        dm.scaledDensity = sysDM.scaledDensity
        dm.densityDpi = sysDM.densityDpi
        appDM.density = sysDM.density
        appDM.scaledDensity = sysDM.scaledDensity
        appDM.densityDpi = sysDM.densityDpi

        XLog.d(TAG, "reset density: ${dm.density}")
        XLog.d(TAG, "reset scaledDensity: ${dm.scaledDensity}")
        XLog.d(TAG, "reset densityDpi: ${dm.densityDpi}")
    }

    fun screenAdaptEnabled(res: Resources = BaseApplication.baseInstance.resources): Boolean {
        return Resources.getSystem().displayMetrics.density != res.displayMetrics.density
    }
}