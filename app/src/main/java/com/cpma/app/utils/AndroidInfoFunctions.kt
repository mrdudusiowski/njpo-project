package com.cpma.app.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.os.BatteryManager
import android.os.Build
import android.view.Display
import android.view.WindowManager


fun getBatteryLevel(mContext: Context): Float {
    val batteryIntent: Intent = mContext.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))!!
    val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
    val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
    return level.toFloat() / scale.toFloat() * 100.0f
}

fun getScreenWidth(mContext: Context): Int {
    val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    var width = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
        val size = Point()
        display.getSize(size)
        size.x
    } else {
       display.width
    }
    return width
}

fun getScreenHeight(mContext: Context): Int {
    val wm = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val display = wm.defaultDisplay
    var height = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
        val size = Point()
        display.getSize(size)
        size.y
    } else {
        display.height
    }
    return height
}