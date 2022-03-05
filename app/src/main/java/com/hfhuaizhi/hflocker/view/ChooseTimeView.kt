package com.hfhuaizhi.hflocker.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.hfhuaizhi.hflocker.R
import com.hfhuaizhi.hz_common_lib.onClick
import kotlinx.android.synthetic.main.view_choose_time.view.*

class ChooseTimeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var maxValue = 0
    private var minValue = 0
    private var currentValue = 0
    var onValueChange: ((value: Int) -> Unit)? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_choose_time, this)
        bt_time_add.onClick {
            currentValue++
            updateButtonEnable()
            onValueChange?.invoke(currentValue)
        }
        bt_time_reduce.onClick {
            currentValue--
            updateButtonEnable()
            onValueChange?.invoke(currentValue)
        }
    }

    fun setValueRange(min: Int, max: Int) {
        minValue = min
        maxValue = max
    }

    fun setValue(value: Int) {
        var value = value
        if (value < minValue) {
            value = minValue
        }
        if (value > maxValue) {
            value = maxValue
        }
        currentValue = value
        updateButtonEnable()
    }

    private fun updateButtonEnable() {
        bt_time_add.isEnabled = currentValue != maxValue
        bt_time_reduce.isEnabled = currentValue != minValue
        var hour: String = ((20 + currentValue / 2) % 24).toString()
        val minute: String = if (currentValue % 2 == 0) "00" else "30"
        var extraString = ""
        if (currentValue >= 8) {
            extraString = "次日"
            hour = "0$hour"
        }
        tv_sleep_time.text = "$extraString $hour:$minute"
    }
}