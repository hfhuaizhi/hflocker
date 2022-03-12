package com.hfhuaizhi.hflocker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.hfhuaizhi.hflocker.R
import com.hfhuaizhi.hz_common_lib.onClick
import kotlinx.android.synthetic.main.view_locker_dialog.view.*

class LockerDialogView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    var onConfirmCLick: (() -> Unit)? = null
    var onTimeout: (() -> Unit)? = null

    private var mHandler = android.os.Handler {
        currentCount--
        if (currentCount == 0) {
            bt_dialog_confirm.setText("确定")
            bt_dialog_confirm.isEnabled = true
        } else {
            bt_dialog_confirm.text = "确定${currentCount}"
            startCount()
        }
        true
    }
    private val PROGRESS_COUNT = 5
    private var currentCount = PROGRESS_COUNT

    init {
        LayoutInflater.from(context).inflate(R.layout.view_locker_dialog, this)
        bt_dialog_cancel.onClick {
            mHandler.removeCallbacksAndMessages(null)
            visibility = GONE
        }
        bt_dialog_confirm.onClick {
            mHandler.removeCallbacksAndMessages(null)
            visibility = GONE
            onConfirmCLick?.invoke()
        }
        visibility = GONE
    }

    fun startProgress() {
        mHandler.removeCallbacksAndMessages(null)
        currentCount = PROGRESS_COUNT
        bt_dialog_confirm.isEnabled = false
        bt_dialog_confirm.text = "确定${currentCount}"
        visibility = VISIBLE
        startCount()
    }

    fun setContentText(content: String) {
        tv_dialog_content.text = content
    }

    private fun startCount() {
        mHandler.postDelayed({
            mHandler.removeCallbacksAndMessages(null)
            visibility = GONE
            onTimeout?.invoke()
        }, PROGRESS_COUNT * 2000L)
        mHandler.sendEmptyMessageDelayed(0, 1000)
    }
}