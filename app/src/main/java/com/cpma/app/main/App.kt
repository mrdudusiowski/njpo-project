package com.cpma.app.main

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App : Application() {
    companion object {
        const val MAIN_CHANNEL_ID = "CPMA_MAIN_NOTIFICATION_CHANNEL"
        const val CHANNEL_ID = "CPMA_NOTIFICATION_CHANNEL"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                    MAIN_CHANNEL_ID,
                    "Main CPMA Channel",
                    NotificationManager.IMPORTANCE_LOW
            )
            val notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    "Notyfication CPMA Channel",
                    NotificationManager.IMPORTANCE_HIGH
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
            manager.createNotificationChannel(notificationChannel)
        }
    }
}