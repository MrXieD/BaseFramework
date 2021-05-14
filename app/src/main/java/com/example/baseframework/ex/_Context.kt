package com.example.baseframework.ex

import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.app.*
import android.app.admin.DevicePolicyManager
import android.app.job.JobScheduler
import android.app.usage.UsageStatsManager
import android.appwidget.AppWidgetManager
import android.bluetooth.BluetoothManager
import android.content.ClipboardManager
import android.content.Context
import android.content.RestrictionsManager
import android.content.pm.LauncherApps
import android.content.pm.PackageInfo
import android.graphics.Point
import android.hardware.ConsumerIrManager
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.hardware.usb.UsbManager
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaRouter
import android.media.projection.MediaProjectionManager
import android.media.session.MediaSessionManager
import android.media.tv.TvInputManager
import android.net.ConnectivityManager
import android.net.nsd.NsdManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NfcManager
import android.os.*
import android.os.storage.StorageManager
import android.print.PrintManager
import android.telecom.TelecomManager
import android.telephony.CarrierConfigManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.TypedValue
import android.util.TypedValue.applyDimension
import android.view.LayoutInflater
import android.view.WindowInsets
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.CaptioningManager
import android.view.inputmethod.InputMethodManager
import android.view.textservice.TextServicesManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.baseframework.ui.*
import java.util.*
// px = dp * density

fun Context.px2dip(px: Float): Float = px / resources.displayMetrics.density + 0.5f

fun Context.px2dip(px: Int): Int = px2dip(px.toFloat()).toInt()

fun Context.dip2px(dp: Float): Float = applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics) //dp * resources.displayMetrics.density + 0.5f

fun Context.dip2px(dp: Int): Int = applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt() //dip2px(dp.toFloat()).toInt()

fun Context.sp2px(sp: Float): Float = applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics) //sp * resources.displayMetrics.density

fun Context.sp2px(sp: Int): Int = applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), resources.displayMetrics).toInt() // sp2px(sp.toFloat()).toInt()


fun Context.toast(msg: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, msg, duration).show()

fun Context.toast(resId: Int, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, resId, duration).show()

fun Context.getResourcesColor(color_name: Int) = ContextCompat.getColor(this,color_name)
/**
 * 获取App的显示区域
 *
 * @return point.x:App显示的宽度 point.y:App显示的高度
 *
 * @see screenSize
 */
val Context.appDisplaySize: Point
    get() {
        val p = Point()
        val wm = windowManager ?: return p
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = wm.currentWindowMetrics
            val windowInsets = metrics.windowInsets
            val insets = windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout())
            val insetsWidth = insets.right + insets.left
            val insetsHeight = insets.top + insets.bottom
            val bounds = metrics.bounds
            p.x = bounds.width() - insetsWidth
            p.y = bounds.height() - insetsHeight
        } else {
            @Suppress("DEPRECATION")
            wm.defaultDisplay?.getSize(p)
        }
        return p

//        return when (resources.configuration.orientation) {
//            Configuration.ORIENTATION_LANDSCAPE -> Point(max(p.x, p.y), min(p.x, p.y)) // 横屏
//            else -> Point(min(p.x, p.y), max(p.x, p.y)) // 竖屏
//        }
    }

/**
 * 获取屏幕的显示区域
 *
 * @return point.x:屏幕宽度 point.y:屏幕高度
 *
 * @see appDisplaySize
 */
val Context.screenSize: Point
    get() {
        val p = Point()
        val wm = windowManager ?: return p
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = wm.currentWindowMetrics.bounds
            p.x = bounds.width()
            p.y = bounds.height()
        } else {
            @Suppress("DEPRECATION")
            wm.defaultDisplay?.getRealSize(p)
        }
        return p

    }

val Context.statusBarHeight: Int
    get() {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = runCatching { resources.getDimensionPixelSize(resourceId) }.getOrDefault(0)
        }

        if (result == 0) {
            result = dip2px(24)
        }

        return result
    }

inline fun Context.version(c: (name: String, code: Long) -> Unit) {
    try {
        this.packageManager.getPackageInfo(this.packageName, 0).version(c)
    } catch (e: Exception) {
        c("unknown", 0L)
    }
}

@Suppress("HasPlatformType")
val Context.versionName get() = try {
    this.packageManager.getPackageInfo(this.packageName, 0).versionName
} catch (e: Exception) {
    "unknown"
}

val Context.versionCode get() = try {
    this.packageManager.getPackageInfo(this.packageName, 0).getVersionCode()
} catch (e: Exception) {
    0L
}

inline fun PackageInfo.version(c: (name: String, code: Long) -> Unit) {
    c(this.versionName, this.getVersionCode())
}

@Suppress("DEPRECATION")
@SuppressLint("NewApi")
inline fun PackageInfo.getVersionCode(): Long {
    return if (isPie) this.longVersionCode else this.versionCode.toLong()
}


val Context.windowManager get() = getSystemService<WindowManager>()
val Context.clipboardManager get() = getSystemService<ClipboardManager>()
val Context.layoutInflater get() = getSystemService<LayoutInflater>()
val Context.activityManager get() = getSystemService<ActivityManager>()
val Context.powerManager get() = getSystemService<PowerManager>()
val Context.alarmManager get() = getSystemService<AlarmManager>()
val Context.notificationManager get() = getSystemService<NotificationManager>()
val Context.nfcManager get() = getSystemService<NfcManager>()
val Context.keyguardManager get() = getSystemService<KeyguardManager>()
val Context.locationManager get() = getSystemService<LocationManager>()
val Context.searchManager get() = getSystemService<SearchManager>()
val Context.storageManager get() = getSystemService<StorageManager>()
val Context.vibrator get() = getSystemService<Vibrator>()
val Context.usbManager get() = getSystemService<UsbManager>()
val Context.connectivityManager get() = getSystemService<ConnectivityManager>()
val Context.wifiManager get() = getSystemService<WifiManager>()
val Context.wifiP2pManager get() = getSystemService<WifiP2pManager>()
val Context.wallpaperManager get() = getSystemService<WallpaperManager>()
val Context.audioManager get() = getSystemService<AudioManager>()
val Context.telephonyManager get() = getSystemService<TelephonyManager>()
val Context.sensorManager get() = getSystemService<SensorManager>()
val Context.uiModeManager get() = getSystemService<UiModeManager>()
val Context.textServicesManager get() = getSystemService<TextServicesManager>()
val Context.downloadManager get() = getSystemService<DownloadManager>()
val Context.accessibilityManager get() = getSystemService<AccessibilityManager>()
val Context.inputMethodManager get() = getSystemService<InputMethodManager>()
val Context.dropBoxManager get() = getSystemService<DropBoxManager>()
val Context.devicePolicyManager get() = getSystemService<DevicePolicyManager>()
val Context.accountManager get() = getSystemService<AccountManager>()

// API >= 16
val Context.inputManager get() = getSystemService<InputManager>()
val Context.mediaRouter get() = getSystemService<MediaRouter>()
val Context.nsdManager get() = getSystemService<NsdManager>()

// API >= 17
val Context.displayManager get() = getSystemService<DisplayManager>()
val Context.userManager get() = getSystemService<UserManager>()

// API >= 18
val Context.bluetoothManager get() = getSystemService<BluetoothManager>()

// API >= 19
val Context.appOpsManager get() = getSystemService<AppOpsManager>()
val Context.captioningManager get() = getSystemService<CaptioningManager>()
val Context.consumerIrManager get() = getSystemService<ConsumerIrManager>()
val Context.printManager get() = getSystemService<PrintManager>()

// API >= 21
val Context.appWidgetManager get() = getSystemService<AppWidgetManager>()
val Context.batteryManager get() = getSystemService<BatteryManager>()
val Context.cameraManager get() = getSystemService<CameraManager>()
val Context.jobScheduler get() = getSystemService<JobScheduler>()
val Context.launcherApps get() = getSystemService<LauncherApps>()
val Context.mediaProjectionManager get() = getSystemService<MediaProjectionManager>()
val Context.mediaSessionManager get() = getSystemService<MediaSessionManager>()
val Context.restrictionsManager get() = getSystemService<RestrictionsManager>()
val Context.telecomManager get() = getSystemService<TelecomManager>()
val Context.tvInputManager get() = getSystemService<TvInputManager>()

// API >= 22
val Context.subscriptionManager get() = getSystemService<SubscriptionManager>()
val Context.usageStatsManager get() = getSystemService<UsageStatsManager>()

// API >= 23
val Context.carrierConfigManager get() = getSystemService<CarrierConfigManager>()

inline fun <reified T> Context.getSystemService(): T? =
    ServiceHelper.getSystemService(
        this,
        T::class.java
    )

object ServiceHelper {

    /**
     * Return the handle to a system-level service by class.
     *
     * @param context Context to retrieve service from.
     * @param serviceClass The class of the desired service.
     * @return The service or null if the class is not a supported system service.
     *
     * @see Context.getSystemService
     */
    @SuppressLint("NewApi")
    fun <T> getSystemService(context: Context, serviceClass: Class<T>): T? {
        if (isMarshmallow) {
            return context.getSystemService(serviceClass)
        }

        val serviceName = LegacyServiceMapHolder.SERVICES[serviceClass] ?: throw Exception("$serviceClass not found")
        @Suppress("UNCHECKED_CAST")
        return context.getSystemService(serviceName) as T?
    }

//    /**
//     * Gets the name of the system-level service that is represented by the specified class.
//     *
//     * @param context Context to retrieve service name from.
//     * @param serviceClass The class of the desired service.
//     * @return The service name or null if the class is not a supported system service.
//     *
//     * @see Context.getSystemServiceName
//     */
//    @SuppressLint("NewApi")
//    private fun getSystemServiceName(context: Context, serviceClass: Class<*>): String? {
//        return if (isMarshmallow) {
//            context.getSystemServiceName(serviceClass)
//        } else {
//            LegacyServiceMapHolder.SERVICES[serviceClass]
//        }
//    }

    private object LegacyServiceMapHolder {
        val SERVICES = HashMap<Class<*>, String>()

        init {
            // API低于23才会调用这个类，因此不用收集23及之上的服务
//            if (Build.VERSION.SDK_INT >= 23) {
//                SERVICES[CarrierConfigManager::class.java] = Context.CARRIER_CONFIG_SERVICE
//            }

            @SuppressLint("NewApi")
            if (isLollipopMr1) {
                SERVICES[SubscriptionManager::class.java] = Context.TELEPHONY_SUBSCRIPTION_SERVICE
                SERVICES[UsageStatsManager::class.java] = Context.USAGE_STATS_SERVICE
            }

            @SuppressLint("NewApi")
            if (isLollipop) {
                SERVICES[AppWidgetManager::class.java] = Context.APPWIDGET_SERVICE
                SERVICES[BatteryManager::class.java] = Context.BATTERY_SERVICE
                SERVICES[CameraManager::class.java] = Context.CAMERA_SERVICE
                SERVICES[JobScheduler::class.java] = Context.JOB_SCHEDULER_SERVICE
                SERVICES[LauncherApps::class.java] = Context.LAUNCHER_APPS_SERVICE
                SERVICES[MediaProjectionManager::class.java] = Context.MEDIA_PROJECTION_SERVICE
                SERVICES[MediaSessionManager::class.java] = Context.MEDIA_SESSION_SERVICE
                SERVICES[RestrictionsManager::class.java] = Context.RESTRICTIONS_SERVICE
                SERVICES[TelecomManager::class.java] = Context.TELECOM_SERVICE
                SERVICES[TvInputManager::class.java] = Context.TV_INPUT_SERVICE
            }

            @SuppressLint("NewApi")
            if (isKitKat) {
                SERVICES[AppOpsManager::class.java] = Context.APP_OPS_SERVICE
                SERVICES[CaptioningManager::class.java] = Context.CAPTIONING_SERVICE
                SERVICES[ConsumerIrManager::class.java] = Context.CONSUMER_IR_SERVICE
                SERVICES[PrintManager::class.java] = Context.PRINT_SERVICE
            }

            @SuppressLint("NewApi")
            if (isJellyBeanMr2) {
                SERVICES[BluetoothManager::class.java] = Context.BLUETOOTH_SERVICE
            }

            @SuppressLint("NewApi")
            if (isJellyBeanMr1) {
                SERVICES[DisplayManager::class.java] = Context.DISPLAY_SERVICE
                SERVICES[UserManager::class.java] = Context.USER_SERVICE
            }

//            @SuppressLint("NewApi")
//            if (isJellyBean) {
                SERVICES[InputManager::class.java] = Context.INPUT_SERVICE
                SERVICES[MediaRouter::class.java] = Context.MEDIA_ROUTER_SERVICE
                SERVICES[NsdManager::class.java] = Context.NSD_SERVICE
//            }

            SERVICES[AccessibilityManager::class.java] = Context.ACCESSIBILITY_SERVICE
            SERVICES[AccountManager::class.java] = Context.ACCOUNT_SERVICE
            SERVICES[ActivityManager::class.java] = Context.ACTIVITY_SERVICE
            SERVICES[AlarmManager::class.java] = Context.ALARM_SERVICE
            SERVICES[AudioManager::class.java] = Context.AUDIO_SERVICE
            SERVICES[ClipboardManager::class.java] = Context.CLIPBOARD_SERVICE
            SERVICES[ConnectivityManager::class.java] = Context.CONNECTIVITY_SERVICE
            SERVICES[DevicePolicyManager::class.java] = Context.DEVICE_POLICY_SERVICE
            SERVICES[DownloadManager::class.java] = Context.DOWNLOAD_SERVICE
            SERVICES[DropBoxManager::class.java] = Context.DROPBOX_SERVICE
            SERVICES[InputMethodManager::class.java] = Context.INPUT_METHOD_SERVICE
            SERVICES[KeyguardManager::class.java] = Context.KEYGUARD_SERVICE
            SERVICES[LayoutInflater::class.java] = Context.LAYOUT_INFLATER_SERVICE
            SERVICES[LocationManager::class.java] = Context.LOCATION_SERVICE
            SERVICES[NfcManager::class.java] = Context.NFC_SERVICE
            SERVICES[NotificationManager::class.java] = Context.NOTIFICATION_SERVICE
            SERVICES[PowerManager::class.java] = Context.POWER_SERVICE
            SERVICES[SearchManager::class.java] = Context.SEARCH_SERVICE
            SERVICES[SensorManager::class.java] = Context.SENSOR_SERVICE
            SERVICES[StorageManager::class.java] = Context.STORAGE_SERVICE
            SERVICES[TelephonyManager::class.java] = Context.TELEPHONY_SERVICE
            SERVICES[TextServicesManager::class.java] = Context.TEXT_SERVICES_MANAGER_SERVICE
            SERVICES[UiModeManager::class.java] = Context.UI_MODE_SERVICE
            SERVICES[UsbManager::class.java] = Context.USB_SERVICE
            SERVICES[Vibrator::class.java] = Context.VIBRATOR_SERVICE
            SERVICES[WallpaperManager::class.java] = Context.WALLPAPER_SERVICE
            SERVICES[WifiP2pManager::class.java] = Context.WIFI_P2P_SERVICE
            SERVICES[WifiManager::class.java] = Context.WIFI_SERVICE
            SERVICES[WindowManager::class.java] = Context.WINDOW_SERVICE
        }
    }
}

@SuppressLint("NewApi")
inline fun Activity.isFinished(): Boolean {
    return this.isFinishing || (isJellyBeanMr1 && this.isDestroyed)
}