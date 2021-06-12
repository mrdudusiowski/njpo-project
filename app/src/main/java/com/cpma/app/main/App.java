package com.cpma.app.main;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


public class App extends Application {

    public static final String MAIN_CHANNEL_ID = "CPMA_MAIN_NOTIFICATION_CHANNEL";
    public static final String CHANNEL_ID = "CPMA_NOTIFICATION_CHANNEL";

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    MAIN_CHANNEL_ID,
                    "Main CPMA Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            NotificationChannel notificationChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notyfication CPMA Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
