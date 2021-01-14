package com.example.baseframework.installer

class InstallerInfo {
    companion object {
        const val STATE_IDLE = 0
        const val STATE_DOWNLOADING = 1
        const val STATE_DOWNLOAD_SUCCESS = 2
        const val STATE_DOWNLOAD_FAIL = 3
    }
    @Volatile
    private var currState = STATE_IDLE
}