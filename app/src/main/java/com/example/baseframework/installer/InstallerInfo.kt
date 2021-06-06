package com.example.baseframework.installer

import android.annotation.SuppressLint
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.example.baseframework.BaseApplication
import com.example.baseframework.ui.isPie
import com.example.baseframework.utils.MD5Utils
import java.io.File

class InstallerInfo(val mUrl: String,
                    private val mMD5: String?,
                    val apkSavePath: String,
                    val mIsAutoInstall: Boolean = false
) {
    var apkPackageName: String? = null
    var errMsg: String? = null

    @Volatile
    var currState = STATE_IDLE

    companion object {
        const val STATE_IDLE = 0
        const val STATE_DOWNLOADING = 1
        const val STATE_DOWNLOAD_SUCCESS = 2
        const val STATE_DOWNLOAD_FAIL = 3
    }

    /**
     * 本地APK包检查，如果没问题返回空字符串，否则返回错误原因。
     */
    fun apkExistCheck(path: String? = null): String {
        val apkSavePath = path ?: apkSavePath
        val apkFile = File(apkSavePath)
        if (!apkFile.exists() || apkFile.length() == 0L) {
            apkFile.delete()
            return "Apk not exist"
        }

        if (!mMD5.isNullOrEmpty()) {
            val downloadMd5 = MD5Utils.fileMd5(apkSavePath)
            if (mMD5 != downloadMd5) {
                apkFile.delete()
                return "md5 error(apkMd5=${mMD5}, downloadMd5=$downloadMd5)"
            }
            return ""
        }

        val pm = BaseApplication.context.packageManager
        val info = try {
            pm.getPackageArchiveInfo(apkSavePath, PackageManager.GET_ACTIVITIES)
        } catch (e: Exception) {
            null
        }
        if (info == null) {
            apkFile.delete()
            return "Apk parse error"
        }

        if (apkPackageName.isNullOrEmpty()) {
            return ""
        }

        try {
            val oldVersion = getVersionCode(pm.getPackageInfo(apkPackageName!!, 0))
            val newVersion = getVersionCode(info)
            if (newVersion < oldVersion) {
                apkFile.delete()
                return "version error(oldVersion=$oldVersion, newVersion=$newVersion)"
            }
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return ""
    }

    @Suppress("DEPRECATION")
    @SuppressLint("NewApi")
    private fun getVersionCode(info: PackageInfo): Long {
        return if (isPie) info.longVersionCode else info.versionCode.toLong()
    }
}