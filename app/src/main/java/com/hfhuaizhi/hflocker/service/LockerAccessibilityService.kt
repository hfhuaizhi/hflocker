package com.hfhuaizhi.hflocker.service

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.hfhuaizhi.hflocker.utils.MemoCache
import com.hfhuaizhi.hflocker.utils.SlideActionHandler
import com.hfhuaizhi.hflocker.utils.SlideActionManager
import com.hfhuaizhi.hflocker.utils.WindowManagerWrapper


class LockerAccessibilityService : AccessibilityService() {


    companion object {
        @SuppressLint("StaticFieldLeak")
        private var accContext: Context? = null
        val isAccOn get() = accContext != null
    }

    override fun onCreate() {
        accContext = this
        WindowManagerWrapper.initWindowManager(applicationContext)
        WindowManagerWrapper.initAccWindowManager(this)
        try {
            checkAndStartService()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onCreate()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        try {
            checkAndStartService()
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun onInterrupt() {
    }


    override fun onDestroy() {
        accContext = null
        WindowManagerWrapper.initAccWindowManager(null)
        super.onDestroy()
    }

    private fun checkAndStartService() {
        if (!MemoCache.haveInit) {
            MemoCache.init()
        }
        if (MemoCache.funcSwitch && !LockerService.isServiceRunning) {
            LockerService.startService(this)
        }
    }
}