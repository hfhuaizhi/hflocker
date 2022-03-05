package com.hfhuaizhi.hflocker.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.hfhuaizhi.hflocker.R
import com.hfhuaizhi.hflocker.app.LockerApplication
import com.hfhuaizhi.hflocker.utils.*
import com.hfhuaizhi.hflocker.view.LockerDialogView
import com.hfhuaizhi.hz_common_lib.onClick
import com.hfhuaizhi.slide.receiver.ScreenBroadcastReceiver


class LockerService : Service() {

    private lateinit var mHandler: Handler
    private lateinit var floatParam: WindowManager.LayoutParams
    private lateinit var floatView: View
    private lateinit var dialogView: LockerDialogView
    private var floatShow = false
    var currentExitStep = 0


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        SlideActionManager.regist(this, listOf(LockerActionHandler()))
        mHandler = Handler(this.mainLooper)
        initService()
        initFloatParam()
        initFloatView()
        registerListener()

    }


    private fun registerListener() {
        ScreenBroadcastReceiver.registerReceiver(
            this,
            object : ScreenBroadcastReceiver.ScreenListener {
                override fun on() {
                    checkFloatShow()
                }

                override fun off() {

                }
            })
    }

    private fun checkFloatShow() {
        Log.e("hftest", "checkFloat")
        if (isInLockTime()) {
            Log.e("hftest", "showFloat")
            showFloat()
        } else {
            Log.e("hftest", "hideFloat")
            hideFloat()
        }
    }

    private fun isInLockTime(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minute = Calendar.getInstance().get(Calendar.MINUTE)
        Log.e("hftest", "hour:$hour")
        Log.e("hftest", "minute:$minute")
        var transHour = 0
        if (hour <= 8) {
            transHour = hour * 2 + 8
        } else {
            transHour = (hour - 20) * 2
        }
        if (minute >= 30) {
            transHour++
        }
        Log.e("hftest", "transHour:$transHour")
        return transHour in MemoCache.sleepTime until MemoCache.wakeupTime
    }

    private fun showFloat() {
        if (floatShow) return
        floatShow = true
        WindowManagerWrapper.addView(floatView, floatParam)
    }

    private fun hideFloat() {
        if (!floatShow) return
        floatShow = false
        WindowManagerWrapper.removeView(floatView)
    }

    override fun onDestroy() {
        ScreenBroadcastReceiver.unRegisterReceiver(this)
        SlideActionManager.unregister(this)
        super.onDestroy()
    }


    private fun initFloatParam() {
        floatParam = WindowManagerWrapper.generateLayoutParams(touchable = true, fullScreen = true)
    }

    private fun initFloatView() {
        floatView = LayoutInflater.from(this).inflate(R.layout.view_locker, null)
        if (MainUtils.isDebug()) {
            floatView.findViewById<View>(R.id.iv_float_icon).setOnLongClickListener {
                hideFloat()
                true
            }
        }
        dialogView = floatView.findViewById(R.id.ldv_float)
        floatView.findViewById<View>(R.id.iv_float_icon).onClick {
            Toast.makeText(applicationContext, "别点我，快睡觉！！", Toast.LENGTH_SHORT).show()
        }
        floatView.findViewById<View>(R.id.iv_float_exit).onClick {
            startExitProgress()
        }
    }

    private fun startExitProgress() {
        currentExitStep = 0
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        return START_REDELIVER_INTENT
    }


    private fun initService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //设定的通知渠道名称
            val channelName = "channelName"
            //构建通知渠道
            @SuppressLint("WrongConstant") val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    channelName,
                    NotificationManager.IMPORTANCE_MIN
                )
            channel.description = "description"
            //在创建的通知渠道上发送通知
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            builder.setSmallIcon(R.mipmap.ic_launcher) //设置通知图标
                .setColor(Color.GRAY)
                .setContentText("slide service is running") //设置通知内容
                .setOngoing(true) //设置处于运行状态
            //向系统注册通知渠道，注册后不能改变重要性以及其他通知行为
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            //将服务置于启动状态 NOTIFICATION_ID指的是创建的通知的ID
            startForeground(NOTIFICATION_ID, builder.build())
        }
    }

    inner class LockerActionHandler : SlideActionHandler {
        override fun handle(intent: Intent) {
            when (intent.action) {
                STOP_SELF -> {
                    stopForeground(true)
                    stopSelf()
                }
            }

        }
    }


    companion object {
        const val STOP_SELF = "STOP_SELF"
        const val NOTIFICATION_ID = 4535
        const val CHANNEL_ID = "4535"
        val isServiceRunning: Boolean
            get() = ServiceUtils.isServiceRunning(
                LockerApplication.context,
                LockerService::class.java.canonicalName
            )

        fun startService(context: Context) {
            val intent = Intent(context, LockerService::class.java)
            if (Build.VERSION.SDK_INT >= 26) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

    }

}