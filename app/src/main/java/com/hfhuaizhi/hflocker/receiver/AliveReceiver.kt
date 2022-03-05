package com.hfhuaizhi.slide.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.hfhuaizhi.hflocker.service.LockerService
import com.hfhuaizhi.hflocker.utils.PermissionUtil
import com.hfhuaizhi.hflocker.utils.WindowManagerWrapper

/**
 * @author hfhuaizhi
 * @date 2020/10/4 12:58
 */
class AliveReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (PermissionUtil.checkAlertWindowsPermission(context) && PermissionUtil.checkUsagePermission(
                context
            )
        ) {
            WindowManagerWrapper.initWindowManager(context)
            LockerService.startService(context)
        }
    }
}