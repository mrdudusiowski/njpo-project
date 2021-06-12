package com.cpma.app.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.cpma.app.R;

public class NotificationProvider {

    private static final String GROUP = "CPMA_GROUP";

    public static NotificationCompat.Builder getNotyfication(Context context, String channelID, String title, String text){
        return new NotificationCompat.Builder(context, channelID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setContentTitle(title)
                .setContentText(text)
                .setGroupSummary(true)
                .setGroup(GROUP)
                .setSmallIcon(R.drawable.ic_logo);
    }

    public static NotificationCompat.Builder getNotyficationRunActivity(Context context, Intent intent, String channelID, String title, String text){

        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent, 0);

        return  new NotificationCompat.Builder(context, channelID)
                .setContentTitle(title)
                .setContentText(text)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))
                .setDefaults(Notification.DEFAULT_ALL)
                .setGroupSummary(true)
                .setGroup(GROUP)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pendingIntent);
    }
}
