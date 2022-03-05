package com.hfhuaizhi.hflocker.utils

import android.annotation.SuppressLint
import android.content.Context
import com.hfhuaizhi.hflocker.app.LockerApplication


@SuppressLint("StaticFieldLeak")
object AppConfig {
    private const val SLEEP_TIME = "SLEEP_TIME"
    private const val WAKEUP_TIME = "WAKEUP_TIME"
    private val mContext: Context = LockerApplication.context
    var sleepTime: Int
        get() = SpUtil.getInt(mContext, SLEEP_TIME, 4)
        set(value) {
            MemoCache.sleepTime = value
            SpUtil.putInt(mContext, SLEEP_TIME, value)
        }

    var wakeupTime: Int
        get() = SpUtil.getInt(mContext, WAKEUP_TIME, 22)
        set(value) {
            MemoCache.sleepTime = value
            SpUtil.putInt(mContext, WAKEUP_TIME, value)
        }
}