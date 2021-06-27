package com.cpma.app.utils

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.cpma.app.R

object NotificationProvider {

   private const val GROUP = "CPMA_GROUP"


    fun getNotification(context: Context, channelID: String, title: String, text: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelID)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            .setContentTitle(title)
            .setContentText(text)
            .setGroupSummary(true)
            .setGroup(GROUP)
            .setSmallIcon(R.drawable.ic_logo)
    }

    fun getNotificationRunActivity(context: Context, intent: Intent, channelID: String, title: String, text: String): NotificationCompat.Builder {
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        return NotificationCompat.Builder(context, channelID)
            .setContentTitle(title)
            .setContentText(text)
            .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
            .setDefaults(Notification.DEFAULT_ALL)
            .setGroupSummary(true)
            .setGroup(GROUP)
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
    }
}