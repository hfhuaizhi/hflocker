package com.hfhuaizhi.hflocker.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Display
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager


/**
 * @author hfhuaizhi
 * @date 2021/1/23 11:00
 */
@SuppressLint("StaticFieldLeak")
object WindowManagerWrapper : WindowManager {
    private var windowManager: WindowManager? = null
    private var accWindowManager: WindowManager? = null
    lateinit var context: Context

    // 初始化 辅助功能 windowManager
    fun initAccWindowManager(context: Context?) {
        accWindowManager = if (context == null) {
            null
        } else {
            context.getSystemService(Application.WINDOW_SERVICE) as WindowManager
        }
    }

    fun initWindowManager(context: Context) {
        WindowManagerWrapper.context = context
        windowManager = context.getSystemService(Application.WINDOW_SERVICE) as WindowManager
    }

    override fun addView(view: View?, params: ViewGroup.LayoutParams?) {
        try {
            if (view != null && PermissionUtil.checkAlertWindowsPermission(context)) {
                if (((params as? WindowManager.LayoutParams)?.type == WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY) && canAddAcc()) {
                    accWindowManager?.addView(view, params)
                } else {
                    windowManager?.addView(view, params)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun updateViewLayout(view: View?, params: ViewGroup.LayoutParams?) {
        try {
            if (view != null) {
                windowManager?.updateViewLayout(view, params)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        try {
            if (view != null) {
                accWindowManager?.updateViewLayout(view, params)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    override fun removeView(view: View?) {
        try {
            if (view != null) {
                windowManager?.removeView(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            if (view != null) {
                accWindowManager?.removeView(view)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getDefaultDisplay(): Display = windowManager?.defaultDisplay!!

    override fun removeViewImmediate(view: View?) {
        try {
            if (view != null) {
                windowManager?.removeViewImmediate(view)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    /**
     * touchable 是否可触摸
     * adjustPan 是否随着输入法移动
     * fullScreen 是否全屏
     * alpha 是否半透明
     * acc 辅助功能层级的悬浮窗
     */
    @SuppressLint("WrongConstant")
    fun generateLayoutParams(
        touchable: Boolean = false,
        adjustPan: Boolean = true,
        fullScreen: Boolean = false,
        alpha: Boolean = false,
        acc: Boolean = false
    ): WindowManager.LayoutParams {
        val params = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            params.type =
                if (acc && canAddAcc()) WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY else WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            if (alpha) {
                params.alpha = 0.79f
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            params.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        params.format = PixelFormat.RGBA_8888
        if (touchable) {
            params.flags =
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        }
        if (adjustPan) {
            params.flags =
                params.flags or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN or WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
        }
        if (fullScreen) {
            params.flags =
                params.flags or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR
        }
        if (Build.VERSION.SDK_INT >= 28) {
            params.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        return params
    }

    // 可以用辅助功能的window type
    private fun canAddAcc(): Boolean {
        return accWindowManager != null
    }
}