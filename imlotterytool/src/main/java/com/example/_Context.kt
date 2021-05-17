package com.example

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
import java.util.*
// px = dp * density

fun Context.px2dip(px: Float): Float = px / resources.displayMetrics.density + 0.5f

fun Context.px2dip(px: Int): Int = px2dip(px.toFloat()).toInt()

fun Context.dip2px(dp: Float): Float = applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.displayMetrics) //dp * resources.displayMetrics.density + 0.5f

fun Context.dip2px(dp: Int): Int = applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt() //dip2px(dp.toFloat()).toInt()

fun Context.sp2px(sp: Float): Float = applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, resources.displayMetrics) //sp * resources.displayMetrics.density

