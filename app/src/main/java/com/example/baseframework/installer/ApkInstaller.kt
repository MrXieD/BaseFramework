package com.example.baseframework.installer

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.example.baseframework.BaseApplication
import com.example.baseframework.ex.DirUtils
import com.example.baseframework.http.NetManager
import com.example.baseframework.http.Request
import com.example.baseframework.http.Response
import com.example.baseframework.http.interfaces.callback.OnFileDownloadListener
import com.example.baseframework.log.XLog
import java.io.File
import java.util.*

/**
 * APP 下载安装器
 *
 */
class ApkInstaller private constructor(private val installerInfo: InstallerInfo) {
    private var mListener: OnInstallerListener? = null

    private lateinit var mContext: Context

    @Volatile
    private var isRelease = false
    private var downloadRequest: Request<OnFileDownloadListener, File>? = null
    private val mainHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
        }
    }

    fun start(context: Context) {
        if (isRelease) {
            return
        }
        mContext = context
        installerInfo.currState = InstallerInfo.STATE_IDLE
        //检测APK是否存在
        if (installerInfo.apkExistCheck().apply { XLog.i("apkExistCheck--->Fail ------> $this") }.isEmpty()) {
            installerInfo.currState = InstallerInfo.STATE_DOWNLOAD_SUCCESS
            mListener?.onDownloadSuccess(installerInfo)
            if (installerInfo.mIsAutoInstall) {
                //跳转安装界面
                ApkInstallActivity.toInstall(mContext,installerInfo.apkSavePath) {
                    XLog.i("安装结果---------->$it")
                }
            }
        } else {
            //开始下载
            installerInfo.currState = InstallerInfo.STATE_DOWNLOADING
            mainHandler.post {
                mListener?.onStartDownload()
            }
            downloadRequest = NetManager.downloadFile(installerInfo.mUrl, File(installerInfo.apkSavePath), object : OnFileDownloadListener {
                override fun onProgress(curr: Long, total: Long) {
                    mainHandler.post {
                        XLog.i("onProgress----------> curr-> $curr , total-> $total")
                        mListener?.onDownloadProgress(curr, total)
                    }
                }

                override fun onSuccess(result: File) {
                    //下载成功后，校验APK，成功后是否跳转安装界面
                    val path = result.absolutePath
                    val errMsg = installerInfo.apkExistCheck(path).apply { XLog.i("apkExistCheck--->Fail ------> $this") }
                    if (errMsg.isNotEmpty()) {
                        installerInfo.currState = InstallerInfo.STATE_DOWNLOAD_FAIL
                        installerInfo.errMsg = errMsg
                        mainHandler.post {
                            mListener?.onDownloadFail(installerInfo)
                        }
                        return
                    }
                    installerInfo.currState = InstallerInfo.STATE_DOWNLOAD_SUCCESS
                    mainHandler.post {
                        mListener?.onDownloadSuccess(installerInfo)
                    }
                    if (installerInfo.mIsAutoInstall) {
                        //跳转安装界面
                        ApkInstallActivity.toInstall(mContext,installerInfo.apkSavePath) {

                        }
                    }
                }

                override fun onFailure(error: Response) {
                    if (isRelease) return
                    val errMsg = error.toString()
                    installerInfo.currState = InstallerInfo.STATE_DOWNLOAD_FAIL
                    installerInfo.errMsg = errMsg
                    mainHandler.post {
                        mListener?.onDownloadFail(installerInfo)
                    }
                }
            }).connect()
        }
    }

    fun release() {
        isRelease = true
        mListener = null
        downloadRequest?.cancel()
    }

    fun setOnInstallListener(listener: OnInstallerListener) {
        mListener = listener
    }

    class Builder {
        private var mUrl: String? = null
        private var mMD5: String? = null
        private var mIsAutoInstall = false
        private var mSaveFile = ""
        private var apkPackageName = ""

        /**
         * 设置下载URL
         */
        fun setUrl(url: String): Builder {
            mUrl = url
            return this
        }

        /**
         * 设置安装MD5
         */
        fun setMD5(md5: String): Builder {
            mMD5 = md5
            return this
        }

        fun setPackageName(packageName: String): Builder {
            apkPackageName = packageName
            return this
        }

        /**
         * 是否下载成功后就直接跳转到安装界面
         */
        fun setIsAutoInstall(isAutoInstall: Boolean): Builder {
            mIsAutoInstall = isAutoInstall
            return this
        }

        /**
         * 设置安装包保存路径
         */
        fun setInstallSavePath(installFile: File): Builder {
            mSaveFile = installFile.absolutePath
            return this
        }

        fun builder(): ApkInstaller {
            if (mUrl.isNullOrEmpty()) {
                throw NullPointerException("Download Url is not null")
            }
            val savePath = File(DirUtils.getPersistentDir(BaseApplication.context), "download").absolutePath
            val saveFiles = File(savePath)
            if (!saveFiles.exists()) {
                saveFiles.mkdirs()
            }
            return ApkInstaller(InstallerInfo(mUrl!!, mMD5, getApkSavePath(savePath), mIsAutoInstall))
        }

        /**
         * 获取APK下载保存的绝对路径
         */
        private fun getApkSavePath(apkSavePath: String, ): String {
            if (apkSavePath.endsWith(".apk")) {
                return apkSavePath
            }
            val name = when {
                apkPackageName.isNotEmpty() -> "${apkPackageName}.apk"
                !mMD5.isNullOrEmpty() -> "default.apk"
                else -> "${UUID.randomUUID()}.apk"
            }
            return File(apkSavePath, name).absolutePath
        }

    }
}