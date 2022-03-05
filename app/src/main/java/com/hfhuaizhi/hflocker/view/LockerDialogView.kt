package com.hfhuaizhi.hflocker.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.hfhuaizhi.hz_common_lib.onClick
import kotlinx.android.synthetic.main.view_locker_dialog.view.*

class LockerDialogView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    var onConfirmCLick: (() -> Unit)? = null

    private var mHandler = android.os.Handler {
        currentCount--
        if (currentCount == 0) {
            bt_dialog_confirm.setText("确定")
            bt_dialog_confirm.isEnabled = true
        } else {
            startCount()
        }
        true
    }
    private var currentCount = 5

    init {
        tv_dialog_content
        bt_dialog_cancel.onClick {
            mHandler.removeCallbacksAndMessages(null)
            visibility = GONE
        }
        bt_dialog_confirm.onClick {
            onConfirmCLick?.invoke()
        }
    }

    fun startCount() {
        mHandler.sendEmptyMessageDelayed(0, 1000)
    }
}