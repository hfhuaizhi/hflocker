package com.hfhuaizhi.hflocker.app

import android.app.Application
import android.content.Context
import com.hfhuaizhi.hflocker.utils.MemoCache
import com.hfhuaizhi.hflocker.utils.WindowManagerWrapper

class LockerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        initConfig()
    }

    private fun initConfig() {
        WindowManagerWrapper.initWindowManager(this)
        MemoCache.init()
    }

    companion object {
        public lateinit var context: Context
            private set
    }
}