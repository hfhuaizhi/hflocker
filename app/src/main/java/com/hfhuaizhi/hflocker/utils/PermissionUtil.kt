package com.hfhuaizhi.hflocker.utils

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Binder
import android.os.Build
import android.os.Process
import android.os.SystemClock
import java.util.*

/**
 * @author hfhuaizhi
 * @date 2020/10/4 13:02
 */
object PermissionUtil {
    fun checkAlertWindowsPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            return true
        }
        try {
            val `object` = context.getSystemService(Context.APP_OPS_SERVICE) ?: return false
            val localClass: Class<*> = `object`.javaClass
            val arrayOfClass: Array<Class<*>?> = arrayOfNulls(3)
            arrayOfClass[0] = Integer.TYPE
            arrayOfClass[1] = Integer.TYPE
            arrayOfClass[2] = String::class.java
            val method = localClass.getMethod("checkOp", *arrayOfClass) ?: return false
            val arrayOfObject1 = arrayOfNulls<Any>(3)
            arrayOfObject1[0] = 24
            arrayOfObject1[1] = Binder.getCallingUid()
            arrayOfObject1[2] = context.packageName
            val m = method.invoke(`object`, *arrayOfObject1) as Int
            return m == AppOpsManager.MODE_ALLOWED
        } catch (ex: Exception) {
        }
        return false
    }

    fun checkUsagePermission(context: Context?): Boolean {
        return if (Build.VERSION.SDK_INT >= 21) {
            if (context == null) {
                return false
            }
            var granted = false
            val appOpsMgr = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
            granted = if (AppOpsManager.MODE_ALLOWED == appOpsMgr.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)) {
                true
            } else {
                val now = System.currentTimeMillis()
                @SuppressLint("WrongConstant") val sageStatsManager = context.getSystemService("usagestats") as UsageStatsManager // Context.USAGE_STATS_SERVICE);
                val events = sageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, now - SystemClock.uptimeMillis(), now)
                if (null != events) events !== Collections.EMPTY_LIST else false
            }
            granted
        } else {
            true
        }
    }
}