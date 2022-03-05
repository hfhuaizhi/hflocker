package com.hfhuaizhi.hflocker.utils

import android.content.Intent
/**
 * @author hfhuaizhi
 */
object SlideActionManager {

    private val handlerMap: MutableMap<Any, MutableList<SlideActionHandler>> = mutableMapOf();


    fun regist(group: Any, list: List<SlideActionHandler>) {
        if (handlerMap[group] == null) {
            handlerMap[group] = mutableListOf()
        }
        handlerMap[group]?.addAll(list)
    }

    fun sendAction(key: String, params: Map<String, Any>? = null) {
        val intent = Intent(key)
        params?.forEach {
            when (val tmpValue = it.value) {
                is Int -> {
                    intent.putExtra(it.key, tmpValue)
                }
                is Double -> {
                    intent.putExtra(it.key, tmpValue)
                }
                is String -> {
                    intent.putExtra(it.key, tmpValue)
                }
                is Long -> {
                    intent.putExtra(it.key, tmpValue)
                }
                is Float -> {
                    intent.putExtra(it.key, tmpValue)
                }
                is Boolean -> {
                    intent.putExtra(it.key, tmpValue)
                }
                else -> {
                    intent.putExtra(it.key, tmpValue.toString())
                }
            }
        }
        handlerMap.forEach {
            it.value.forEach { handler ->
                handler.handle(intent)
            }
        }
    }

    fun unregister(group: Any) {
        handlerMap[group]?.clear()
        handlerMap.remove(group)
    }

}

interface SlideActionHandler {
    fun handle(intent: Intent)
}