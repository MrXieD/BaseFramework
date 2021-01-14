package com.example.baseframework.installer

interface OnInstallerListener {
    /**
     * 下载成功
     */
    fun onDownloadSuccess(installerInfo: InstallerInfo)

    /**
     * 下载失败
     */
    fun onDownloadFail(installerInfo: InstallerInfo)

    /**
     * 下载进度
     */
    fun onDownloadProgress(curr:Long,total:Long)

    /**
     * 开始下载
     */
    fun onStartDownload()
}