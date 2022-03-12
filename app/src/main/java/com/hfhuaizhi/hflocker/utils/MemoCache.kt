package com.hfhuaizhi.hflocker.utils

object MemoCache {

    var haveInit = false
    var funcSwitch = false
    var sleepTime = 0
    var wakeupTime = 0
    fun init() {
        if(haveInit) return
        haveInit = true
        sleepTime = AppConfig.sleepTime
        wakeupTime = AppConfig.wakeupTime
        funcSwitch = AppConfig.funcSwitch
    }
}