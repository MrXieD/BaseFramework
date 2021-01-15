package com.example.baseframework.installer

import android.os.Handler
import android.os.Looper
import java.io.File

/**
 * APP 下载安装器
 *
 */
class AppInstaller {
    private constructor(installerInfo: InstallerInfo)
    private var mListener:OnInstallerListener?=null
    private val mainHandler: Handler = Handler(Looper.getMainLooper())
    private lateinit var installerInfo:InstallerInfo
    companion object{
        const val DEFAULT_APK_FILE = ""
    }
    fun start() {

    }

    fun stop() {

    }

    fun release() {

    }

    fun setOnInstallListener(listener:OnInstallerListener){
        mListener = listener
    }

    class Builder {
        private var mUrl:String?=null
        private var mMD5:String? = null
        private var mIsBackground = true
        private var mInstallFile = DEFAULT_APK_FILE
        /**
         * 设置下载URL
         */
        fun setUrl(url:String): Builder {
            return this
        }

        /**
         * 设置安装MD5
         */
        fun setMD5(md5:String): Builder {
            return this
        }

        /**
         * 是否在后台静默下载还是前台下载
         */
        fun setIsBackgroundDownload(isBackground:Boolean): Builder {
            return this
        }

        /**
         * 设置安装包保存路径
         */
        fun setInstallSavePath(installFile: File): Builder {
            return this
        }

        /**
         * 是否下载成功后就直接跳转到安装界面
         */
        fun setAutoInstall(): Builder {
            return this
        }

        fun builder(): AppInstaller {
            return AppInstaller(InstallerInfo())
        }
    }
}