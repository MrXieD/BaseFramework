package com.example.baseframework.utils

import java.io.File
import java.security.MessageDigest

object MD5Utils {
    private val HEX_DIGITS = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')

    /**
     * 取得字符串的md5值，默认是32位的
     */
    fun md5(str: String, bits16: Boolean = false): String {
        return if (bits16) {
            toHexString(md5(str.toByteArray())).substring(8, 24)
        } else {
            toHexString(md5(str.toByteArray()))
        }
    }

    /**
     * 取得data的MD5值
     */
    fun md5(data: ByteArray): ByteArray? = runCatching {
        val digest = MessageDigest.getInstance("MD5")
        digest.update(data)
        digest.digest()
    }.getOrNull()

    /**
     * 取得文件的md5值，默认是32位的
     */
    fun fileMd5(fileName: String, bits16: Boolean = false): String = fileMd5(File(fileName), bits16)

    /**
     * 取得文件的md5值，默认是32位的
     */
    fun fileMd5(file: File, bits16: Boolean = false): String {
        return runCatching {
            val md5 = MessageDigest.getInstance("MD5")
            file.inputStream().use {
                try {
                    val buff = ByteArray(DEFAULT_BUFFER_SIZE)
                    var readNum: Int
                    while (true){
                        readNum = it.read(buff)
                        if(readNum == -1){
                            break
                        }
                        md5.update(buff, 0, readNum)
                    }
                }finally {
                    CloseableUtils.close(it)
                }
            }
            return if (bits16) {
                toHexString(md5.digest()).substring(8, 24)
            } else {
                toHexString(md5.digest())
            }
        }.getOrDefault("")
    }

    private fun toHexString(data: ByteArray?): String {
        if (data == null || data.isEmpty()) {
            return ""
        }

        val sb = StringBuilder(data.size * 2)
        for (b in data) {
            sb.append(HEX_DIGITS[(b.toInt() and 0xff).shr(4)])
            sb.append(HEX_DIGITS[(b.toInt() and 0x0f)])
        }
        return sb.toString()
    }

    fun getSHA1(value: String): String = runCatching {
        val digest = MessageDigest.getInstance("SHA-1")
        digest.update(value.toByteArray())
        val data = digest.digest()
        val sb = StringBuilder()
        var shaHex: String
        for (b in data) {
            shaHex = Integer.toHexString(b.toInt() and 0xFF)
            if (shaHex.length < 2) {
                sb.append(0)
            }
            sb.append(shaHex)
        }
        sb.toString()
    }.getOrDefault("")

}