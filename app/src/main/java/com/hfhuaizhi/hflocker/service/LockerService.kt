package com.hfhuaizhi.hflocker.service

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.icu.util.Calendar
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.hfhuaizhi.hflocker.R
import com.hfhuaizhi.hflocker.app.LockerApplication
import com.hfhuaizhi.hflocker.utils.*
import com.hfhuaizhi.hflocker.view.LockerDialogView
import com.hfhuaizhi.hz_common_lib.onClick
import com.hfhuaizhi.hz_common_lib.setHeight
import com.hfhuaizhi.hz_common_lib.toPx
import com.hfhuaizhi.slide.receiver.ScreenBroadcastReceiver
import java.util.*


class LockerService : Service() {

    private lateinit var mHandler: Handler
    private lateinit var floatParam: WindowManager.LayoutParams
    private lateinit var floatTipsParam: WindowManager.LayoutParams

    private lateinit var floatView: View
    private lateinit var dialogView: LockerDialogView
    private lateinit var lockerNotifyView: View

    @Volatile
    private var mThreadRunning = false

    @Volatile
    private var mThreadPause = false
    private val mWaitObject = Object()
    private var floatShow = false
    private var tipsViewShow = false
    private var justLeave = false
    private var mMonitorThread: Thread? = null
    private val tipsAnim = ValueAnimator.ofArgb(Color.RED, Color.TRANSPARENT).apply {
        repeatCount = Animation.INFINITE
        repeatMode = ValueAnimator.REVERSE
        interpolator = AccelerateDecelerateInterpolator()
        duration = 2000
    }
    var currentExitStep = 0
    val dialogContentList =
        listOf<String>(
            "你有很重要的事情要去做吗？",
            "熬夜会有黑眼圈，而且还会影响第二天的工作和学习，确定继续吗？",
            "真的确定吗？"
        )

    private fun initMonitor() {
        if (mMonitorThread == null || !mMonitorThread!!.isAlive) {
            if (mMonitorThread == null) {
                mMonitorThread = Thread(checkRunnable)
            }
            mMonitorThread?.start()
            mThreadRunning = true
        }
    }

    private val checkRunnable = Runnable {
        while (mThreadRunning) {
            try {
                if (mThreadPause) {
                    synchronized(mWaitObject) { mWaitObject.wait() }
                }
                if (!justLeave) {
                    checkFloatShow()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

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
        initMonitor()
    }


    private fun registerListener() {
        ScreenBroadcastReceiver.registerReceiver(
            this,
            object : ScreenBroadcastReceiver.ScreenListener {
                override fun on() {
                    checkFloatShow()
                    synchronized(mWaitObject) {
                        mThreadPause = false
                        mWaitObject.notify()
                    }
                }

                override fun off() {
                    mThreadPause = true
                    justLeave = false
                }
            })
    }

    private fun checkFloatShow() {
        mHandler.post {
            if (isInLockTime()) {
                showFloat()
            } else {
                hideFloat()
                hideTipsView()
            }
        }
    }

    private fun isInLockTime(): Boolean {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minute = Calendar.getInstance().get(Calendar.MINUTE)
        var transHour = 0
        if (hour <= 8) {
            transHour = hour * 2 + 8
        } else {
            transHour = (hour - 20) * 2
        }
        if (minute >= 30) {
            transHour++
        }
        return transHour in MemoCache.sleepTime until MemoCache.wakeupTime
    }

    private fun showFloat() {
        if (floatShow) return
        floatShow = true
        WindowManagerWrapper.addView(floatView, floatParam)
        hideTipsView()
    }

    private fun hideFloat() {
        if (!floatShow) return
        floatShow = false
        WindowManagerWrapper.removeView(floatView)
    }

    private fun showTipsView() {
        if (tipsViewShow) return
        tipsViewShow = true
        tipsAnim.start()
        WindowManagerWrapper.addView(lockerNotifyView, floatTipsParam)
    }

    private fun hideTipsView() {
        if (!tipsViewShow) return
        tipsViewShow = false
        tipsAnim.pause()
        WindowManagerWrapper.removeView(lockerNotifyView)
    }

    override fun onDestroy() {
        ScreenBroadcastReceiver.unRegisterReceiver(this)
        SlideActionManager.unregister(this)
        isServiceRunning = false
        mThreadRunning = false
        hideFloat()
        hideTipsView()
        super.onDestroy()
    }


    private fun initFloatParam() {
        floatParam = WindowManagerWrapper.generateLayoutParams(
            touchable = true,
            fullScreen = true,
            acc = true
        )

        // tips params
        floatTipsParam =
            WindowManagerWrapper.generateLayoutParams(
                touchable = false,
                adjustPan = false,
                fullScreen = true,
                alpha = true
            )
        floatTipsParam.height = (DensityUtil.getStatusBarHeight(this) + 50.toPx).toInt()
        floatTipsParam.gravity = Gravity.TOP
        floatTipsParam.x = 0
        floatTipsParam.y = 0
    }

    private fun initFloatView() {
        floatView = LayoutInflater.from(this).inflate(R.layout.view_locker, null)
        if (MainUtils.isDebug()) {
            floatView.findViewById<View>(R.id.iv_float_icon).setOnLongClickListener {
                leaveLocker()
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
        // tipsView
        lockerNotifyView = LayoutInflater.from(this).inflate(R.layout.view_tips_view, null)
        val tipsView = lockerNotifyView.findViewById<View>(R.id.v_float_tips_status)
        tipsView.setHeight(DensityUtil.getStatusBarHeight(this))
        tipsAnim.addUpdateListener {
            val currentColor = it.getAnimatedValue() as Int
            tipsView.setBackgroundColor(currentColor)
        }
    }

    private fun leaveLocker() {
        justLeave = true
        hideFloat()
        showTipsView()
    }

    private fun startExitProgress() {
        currentExitStep = 0
        dialogView.setContentText(dialogContentList.get(currentExitStep))
        dialogView.startProgress()
        dialogView.onConfirmCLick = {
            if (++currentExitStep < dialogContentList.size) {
                dialogView.setContentText(dialogContentList[currentExitStep])
                dialogView.startProgress()
            } else {
                leaveLocker()
            }
        }
        dialogView.onTimeout = {
            currentExitStep = 0
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        isServiceRunning = true
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
        var isServiceRunning = false

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