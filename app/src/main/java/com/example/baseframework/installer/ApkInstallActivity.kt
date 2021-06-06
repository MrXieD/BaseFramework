package com.example.baseframework.installer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.baseframework.ex.getVersionCode
import com.example.baseframework.ui.isNougat
import com.example.baseframework.ui.isOreo
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.newTask
import java.io.File

class ApkInstallActivity : AppCompatActivity() {
    companion object {
        private var mGlobalCallback: ((Boolean) -> Unit)? = null

        fun toInstall(context: Context, apkPath: String, onInstallListener: (Boolean) -> Unit) {
            mGlobalCallback = onInstallListener
            try {
                context.startActivity(context.intentFor<ApkInstallActivity>("apk_path" to apkPath).newTask())
            } catch (e: Exception) {
                mGlobalCallback?.invoke(false)
                mGlobalCallback = null
            }
        }
    }
    private var mApkPath = ""
    private var mPackageName = ""
    private var mPackageOldVersion = 0L
    private var lastUpdateTime = 0L
    private var isResume = false
    private var isInstalling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.apply {
            mApkPath = getStringExtra("apk_path")!!
        }
        var info = try {
            packageManager.getPackageArchiveInfo(mApkPath, PackageManager.GET_ACTIVITIES)
        } catch (e: Exception) {
            null
        }
        if (info == null) {
            mGlobalCallback?.invoke(false)
            finish()
            return
        }

        mPackageName = info.packageName
        mPackageOldVersion = info.getVersionCode()

        info = try {
            packageManager.getPackageInfo(mPackageName, 0)
        } catch (e: Exception) {
            null
        }
        if (info != null) {
            lastUpdateTime = info.lastUpdateTime
            if (lastUpdateTime == 0L) {
                lastUpdateTime = info.firstInstallTime
            }
        }
        if (isOreo) {
            if (packageManager.canRequestPackageInstalls()) {
                installApk(mApkPath)
            } else {
                try {
                    startActivityForResult(Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES), 100)
                } catch (e: Exception) {
                    requestPermissions(arrayOf(Manifest.permission.REQUEST_INSTALL_PACKAGES),200)
                }
            }
        } else {
            installApk(mApkPath)
        }
    }


    @SuppressLint("NewApi")
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val map = mutableMapOf<String,Boolean>()
            for (i in permissions.indices){
                map[permissions[i]] = grantResults[i] == PackageManager.PERMISSION_GRANTED
            }
        if (packageManager.canRequestPackageInstalls()) {
            installApk(mApkPath)
        } else {
            mGlobalCallback?.invoke(false)
            finish()
        }

    }


    @SuppressLint("NewApi")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
         if(requestCode == 100){
            if (packageManager.canRequestPackageInstalls()) {
                installApk(mApkPath)
            } else {
                mGlobalCallback?.invoke(false)
                finish()
            }
        }
    }

    private fun installApk(path: String) {
        val apkFile = File(path)
        val intent = Intent(Intent.ACTION_VIEW)
        if (isNougat) {
            val authority = "${packageName}.apkinstall.ApkInstallFileProvider"
            val apkUri = FileProvider.getUriForFile(this, authority, apkFile)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        try {
            startActivity(intent.newTask())
            isInstalling = true
        } catch (e: Exception) {
            mGlobalCallback?.invoke(false)
            finish()
        }
    }
    override fun onResume() {
        super.onResume()
        if (!isResume) {
            isResume = true
            return
        }

        if (isInstalling) {
            val info = try {
                packageManager.getPackageInfo(mPackageName, 0)
            } catch (e: Exception) {
                null
            }

            if (info == null) {
                mGlobalCallback?.invoke(false)
            } else {
                val currVersion = info.getVersionCode()
                when {
                    currVersion > mPackageOldVersion -> mGlobalCallback?.invoke(true)
                    currVersion == mPackageOldVersion -> mGlobalCallback?.invoke(lastUpdateTime < info.lastUpdateTime)
                    else -> mGlobalCallback?.invoke(false)
                }
            }

            finish()
        }
    }

    override fun onDestroy() {
        mGlobalCallback = null
        super.onDestroy()
    }
}