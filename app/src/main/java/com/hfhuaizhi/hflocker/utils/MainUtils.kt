package com.hfhuaizhi.hflocker.utils

import android.content.Context
import android.os.VibrationEffect
import android.provider.Settings
import android.text.TextUtils
import com.hfhuaizhi.hflocker.service.LockerAccessibilityService
import android.content.pm.ApplicationInfo
import com.hfhuaizhi.hflocker.app.LockerApplication


/**
 * @author hfhuaizhi
 * @date 2020/12/6 23:36
 */
object MainUtils {
    fun isAccessibilitySettingsOn(mContext: Context): Boolean {
        var accessibilityEnabled = 0
        val service =
            mContext.packageName + "/" + LockerAccessibilityService::class.java.getCanonicalName()
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                mContext.getApplicationContext().getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED
            )
        } catch (e: Settings.SettingNotFoundException) {
            e.printStackTrace()
        }
        val mStringColonSplitter = TextUtils.SimpleStringSplitter(':')
        if (accessibilityEnabled == 1) {
            val settingValue = Settings.Secure.getString(
                mContext.getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue)
                while (mStringColonSplitter.hasNext()) {
                    val accessibilityService = mStringColonSplitter.next()
                    if (accessibilityService.equals(service, ignoreCase = true)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun haveVibMethod(): Boolean {
        var clazz = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            VibrationEffect::class.java
        } else {
            return false
        }
        var methods = clazz.methods
        for (m in methods) {
            if (m.name == "createPredefined") {
                return true
            }
        }
        return false
    }

    fun isDebug(): Boolean {
        val info: ApplicationInfo = LockerApplication.context.applicationInfo
        return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
    }
}