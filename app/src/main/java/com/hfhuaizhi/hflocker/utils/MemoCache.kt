package com.hfhuaizhi.hflocker.utils

object MemoCache {
    var sleepTime = 0
    var wakeupTime = 0
    fun init() {
        sleepTime = AppConfig.sleepTime
        wakeupTime = AppConfig.wakeupTime
    }
}