package com.hfhuaizhi.hflocker.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import com.hfhuaizhi.hflocker.R
import com.hfhuaizhi.hflocker.service.LockerService
import com.hfhuaizhi.hflocker.utils.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * 前一天八点到第二天八点共24份
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        initView()
    }

    private fun initView() {
        ctv_sleep.setValueRange(0, 8 + 2)
        ctv_sleep.setValue(MemoCache.sleepTime)

        ctv_wake.setValueRange(8 + 10, 8 + 16)
        ctv_wake.setValue(MemoCache.wakeupTime)
        permission_float.onProcessClick = {
            openFloatPermission()
        }
        permission_acc.onProcessClick = {
            openAccPermission()
        }
        ctv_sleep.onValueChange = {
            AppConfig.sleepTime = it
        }
        ctv_wake.onValueChange = {
            AppConfig.wakeupTime = it
        }
        sw_main.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                if (PermissionUtil.checkAlertWindowsPermission(this)) {
                    LockerService.startService(applicationContext)
                    AppConfig.funcSwitch = true
                } else {
                    Toast.makeText(applicationContext, "请先开启必要权限", Toast.LENGTH_SHORT).show()
                    sw_main.isChecked = false
                }
            } else {
                SlideActionManager.sendAction(LockerService.STOP_SELF)
                AppConfig.funcSwitch = false
            }
        }
    }

    fun openFloatPermission() {
        startActivityForResult(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            ), 1
        )
    }

    fun openAccPermission() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onResume() {
        initPermissionState()
        initRunningState()
        super.onResume()
    }

    private fun initRunningState() {
        sw_main.isChecked = LockerService.isServiceRunning
    }

    private fun initPermissionState() {
        permission_float.setOpenedState(PermissionUtil.checkAlertWindowsPermission(this))
        permission_acc.setOpenedState(MainUtils.isAccessibilitySettingsOn(this))
    }
}