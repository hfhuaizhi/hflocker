package com.hfhuaizhi.hflocker.view

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.hfhuaizhi.hflocker.R
import kotlinx.android.synthetic.main.view_slide_process.view.*

/**
 * @author hfhuaizhi
 * @date 2020/12/8 21:48
 */
class SlideProcessView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    val mContext = context
    var onProcessClick: (() -> Unit)? = null
    private var isOpen = false
    private var closeable = true

    init {
        LayoutInflater.from(mContext).inflate(R.layout.view_slide_process, this)
        if (attrs != null) {
            val ta: TypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlideProcessView)
            val title = ta.getString(R.styleable.SlideProcessView_permission_title)
            val content = ta.getString(R.styleable.SlideProcessView_permission_content)
            closeable = ta.getBoolean(R.styleable.SlideProcessView_permission_closeable, true)
            tv_teach_title.text = title
            tv_teach_content.text = content
            ta.recycle()
        }
        ll_permission_container.setOnClickListener {
            if (isOpen && !closeable) {
                return@setOnClickListener
            }
            onProcessClick?.invoke()
        }
    }

    fun setTitle(msg: String) {
        tv_teach_title.text = msg
    }

    fun setSubTitle(msg: String) {
        tv_teach_content.text = msg
    }

    fun getOpenState() = isOpen

    fun isOpenPermission() = isOpen

    fun setOpenedState(open: Boolean) {
        isOpen = open
        ll_permission_container.setContainerColor(if (open) resources.getColor(R.color.service_running) else resources.getColor(R.color.service_error))
    }
}