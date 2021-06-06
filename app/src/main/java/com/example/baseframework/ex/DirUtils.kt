package com.example.baseframework.ex

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import java.io.File

object DirUtils {
    /**
     * 获取缓存目录，优先使用外部缓存，如果外部缓存不可用则使用内部缓存。
     *
     * @param onlyUseInternalCache 只使用内部缓存，默认false
     */
    fun getCacheDir(ctx: Context,onlyUseInternalCache: Boolean = false): File {
        return if (onlyUseInternalCache) {
            ctx.cacheDir.apply { mkdirs() }
        } else {
            (getExtDir(ctx, true) ?: ctx.cacheDir).apply { mkdirs() }
        }
    }

    /**
     * 获取存储目录，优先使用外部存储，如果外部存储不可用则使用内部存储。
     *
     * @param onlyUseInternalFiles 只使用内部存储，默认false
     */
    fun getPersistentDir(ctx: Context,onlyUseInternalFiles: Boolean = false): File {
        return if (onlyUseInternalFiles) {
            ctx.filesDir.apply { mkdirs() }
        } else {
            (getExtDir(ctx, false) ?: ctx.filesDir).apply { mkdirs() }
        }
    }

    /**
     * 获取外部目录
     */
    @SuppressLint("SdCardPath")
    private fun getExtDir(context: Context, isCacheDir: Boolean): File? {
        var dir: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            dir = if (isCacheDir) {
                context.externalCacheDir
            } else {
                context.getExternalFilesDir(null)
            }
            if (dir == null) {
                // 有些手机需要通过自定义目录
                val path = "/sdcard/Android/data/${context.packageName}"
                dir = File("$path/${if (isCacheDir) "cache" else "files"}")
            }
            if (!dir.exists() && !dir.mkdirs()) {
                dir = null
            }
        }
        return dir
    }

}