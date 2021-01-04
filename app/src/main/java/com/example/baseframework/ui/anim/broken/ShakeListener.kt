package com.xiedi.testapp.test.broken

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.SystemClock
import android.util.Log
import com.xiedi.testapp.work.log.XLog
import kotlin.math.sqrt


class ShakeListener(private val context: Context) : SensorEventListener {
    // 速度阈值，当摇晃速度达到这值后产生作用
    private val SPEED_SHRESHOLD = 3000

    // 两次检测的时间间隔
    private val UPTATE_INTERVAL_TIME = 70

    // 传感器管理器
    private var sensorManager: SensorManager? = null

    // 传感器
    private var sensor: Sensor? = null

    // 重力感应监听器
    private var onShakeListener: OnShakeListener? = null


    // 手机上一个位置时重力感应坐标
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    // 上次检测时间
    private var lastUpdateTime: Long = 0

    // 开始
    fun start() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
        XLog
    }

    // 停止检测
    fun stop(isRemove:Boolean) {
        if (isRemove){
            onShakeListener=null
        }
        sensorManager?.unregisterListener(this)

    }

    fun setShakeListener(listener:(Boolean)-> Unit){
        onShakeListener=object :OnShakeListener{
            override fun onShake(direction: Boolean) {
                listener(direction)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {


    }

    override fun onSensorChanged(event: SensorEvent) {
        // 现在检测时间
        val currentUpdateTime = SystemClock.uptimeMillis()
        // 两次检测的时间间隔
        // 两次检测的时间间隔
        val timeInterval = currentUpdateTime - lastUpdateTime
        // 判断是否达到了检测时间间隔
        if (timeInterval < UPTATE_INTERVAL_TIME) return
        // 现在的时间变成last时间
        lastUpdateTime = currentUpdateTime
        // 获得x,y,z坐标
        // 获得x,y,z坐标
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        // 获得x,y,z的变化值
        // 获得x,y,z的变化值
        val deltaX = x - lastX
        val deltaY = y - lastY
        val deltaZ = z - lastZ
        // 将现在的坐标变成last坐标
        lastX = x
        lastY = y
        lastZ = z
        //sqrt 返回最近的双近似的平方根
        val speed = sqrt((deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ).toDouble()) / timeInterval * 10000
        // 达到速度阀值，发出提示
        if (speed >= SPEED_SHRESHOLD) {
            Log.i("onSensorChanged","deltaX--->$deltaX")
            Log.i("onSensorChanged","deltaY--->$deltaY")
            Log.i("onSensorChanged","deltaZ--->$deltaZ")
            Log.i("onSensorChanged","---------------------->${Thread.currentThread().name}")
            //判断是朝左还是朝右摇晃手机
            onShakeListener?.onShake(deltaX>0)
        }
    }

    interface OnShakeListener {
        //ture--》left
        fun onShake(direction:Boolean)
    }
}