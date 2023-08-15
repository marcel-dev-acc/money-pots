package com.marcelm.myfinances.ui.methods

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log

class MyForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var contentText = ""
//        val currencyConversions = fetchStoredAmounts(context)
//        for (currencyConversion in currencyConversions) {
//            contentText = contentText + currencyConversion.srcCurrency + "=>" + currencyConversion.trgtCurrency
//            Log.d("MyForegroundService", contentText)
//        }
        Log.d("MyForegroundService", "started ${globalCurrencyConversionsVariable.toString()}")
        // Create a notification channel if needed (for Android 8+)
        createNotificationChannel()

        // Build a notification for the foreground service
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Money")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .build()

        // Start the service in the foreground with the notification
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)

        // Perform your service logic here

        // Return START_STICKY to indicate that the service should be restarted if terminated
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Foreground Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "MyForegroundServiceChannel"
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}
