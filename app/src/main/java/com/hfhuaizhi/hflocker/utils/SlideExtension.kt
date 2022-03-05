package com.hfhuaizhi.hz_common_lib

import android.content.res.Resources
import android.util.TypedValue
import android.util.TypedValue.COMPLEX_UNIT_DIP
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

/**
 * @author hfhuaizhi
 * @date 2020/12/5 17:02
 */

val Float.dp
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
    )

fun View.onClick(callBack: (v: View) -> Unit) {
    this.setOnClickListener {
        callBack.invoke(it)
    }
}

fun View.onLongClick(callBack: (v: View) -> Unit) {
    this.setOnLongClickListener {
        callBack.invoke(it)
        true
    }
}

fun View.setRlLeftMargin(m: Int) {
    val params = this.layoutParams
    if (params is RelativeLayout.LayoutParams) {
        params.leftMargin = m
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
        params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        layoutParams = params
    }
}

fun View.setLeftMargin(m:Int){
    val params = this.layoutParams
    if(params is ViewGroup.MarginLayoutParams){
        params.leftMargin = m
        layoutParams = params
    }
}

fun View.setRightMargin(m:Int){
    val params = this.layoutParams
    if(params is ViewGroup.MarginLayoutParams){
        params.rightMargin = m
        layoutParams = params
    }
}

val View.leftMargin:Int
        get() {
            val params = this.layoutParams
            return if(params is ViewGroup.MarginLayoutParams) params.leftMargin else 0
        }

val View.rightMargin:Int
    get() {
        val params = this.layoutParams
        return if(params is ViewGroup.MarginLayoutParams) params.rightMargin else 0
    }

fun View.setRlRightMargin(m: Int) {
    val params = this.layoutParams
    if (params is RelativeLayout.LayoutParams) {
        params.rightMargin = m
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
        params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
        layoutParams = params
    }
}

fun View.setHeight(value: Int) {
    val params = this.layoutParams
    if (params != null) {
        params.height = value
        layoutParams = params
    }
}

fun View.setWidth(value: Int) {
    val params = this.layoutParams
    if (params != null) {
        params.width = value
        layoutParams = params
    }
}

fun View.setBottomMargin(value: Int) {
    val params = this.layoutParams
    if (params != null&&params is ViewGroup.MarginLayoutParams) {
        params.bottomMargin = value
        layoutParams = params
    }
}

val Float.toPx: Float
    get() = TypedValue.applyDimension(COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics)

val Int.toPx: Float
    get() = TypedValue.applyDimension(
        COMPLEX_UNIT_DIP,
        this.toFloat(),
        Resources.getSystem().displayMetrics
    )

inline fun <T1 : Any, T2 : Any, R : Any> safeLet(p1: T1?, p2: T2?, block: (T1, T2) -> R?): R? {
    return if (p1 != null && p2 != null) block(p1, p2) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, R : Any> safeLet(p1: T1?, p2: T2?, p3: T3?, block: (T1, T2, T3) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null) block(p1, p2, p3) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, R : Any> safeLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, block: (T1, T2, T3, T4) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null) block(p1, p2, p3, p4) else null
}

inline fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, R : Any> safeLet(p1: T1?, p2: T2?, p3: T3?, p4: T4?, p5: T5?, block: (T1, T2, T3, T4, T5) -> R?): R? {
    return if (p1 != null && p2 != null && p3 != null && p4 != null && p5 != null) block(p1, p2, p3, p4, p5) else null
}