package com.hfhuaizhi.slide.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ScreenBroadcastReceiver : BroadcastReceiver() {
    private var action: String? = null
    override fun onReceive(context: Context, intent: Intent) {
        action = intent.action
        if (Intent.ACTION_SCREEN_OFF == action) { // 锁屏
            listenerMap.forEach {
                it.value?.off()
            }
        } else if (Intent.ACTION_USER_PRESENT == action) { // 解锁
            listenerMap.forEach {
                it.value?.on()
            }
        }
    }

    interface ScreenListener {
        fun on()
        fun off()
    }

    companion object {
        private val receiverMap: MutableMap<Context, ScreenBroadcastReceiver> = mutableMapOf()
        private val listenerMap: MutableMap<Context, ScreenListener?> = mutableMapOf()

        @JvmStatic
        fun registerReceiver(context: Context, listener: ScreenListener?) {
            listenerMap[context] = listener
            val filter = IntentFilter()
            filter.addAction(Intent.ACTION_SCREEN_OFF)
            filter.addAction(Intent.ACTION_USER_PRESENT)
            if (receiverMap[context] == null) {
                receiverMap[context] = ScreenBroadcastReceiver()
            }
            context.registerReceiver(receiverMap[context], filter)
        }

        @JvmStatic
        fun unRegisterReceiver(context: Context) {
            if (receiverMap[context] != null) {
                context.unregisterReceiver(receiverMap[context])
                receiverMap.remove(context)
                listenerMap.remove(context)
            }
        }
    }
}