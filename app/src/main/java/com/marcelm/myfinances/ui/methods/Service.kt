package com.marcelm.myfinances.ui.methods

import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.util.Log
import com.marcelm.myfinances.R

class MyForegroundService : Service(), GlobalCurrencyConversionChangeListener {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Initialize the notification manager and builder
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationBuilder = createNotificationBuilder()

        // Start the service in the foreground with the initial notification
        startForeground(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build())

        // Return START_STICKY to indicate that the service should be restarted if terminated
        return START_STICKY
    }

    override fun onGlobalCurrencyConversionsChanged(newCurrencyConversions: Map<String, CurrencyConversion>) {
        Log.d("MyForegroundService", "new content ${newCurrencyConversions.toString()}")
        // Update the notification content based on the new currency conversions
        val contentText = buildNotificationContent(newCurrencyConversions)
        notificationBuilder.setContentText(contentText)

        // Notify the notification manager to update the notification
        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun buildNotificationContent(currencyConversions: Map<String, CurrencyConversion>): String {
        val contentText = StringBuilder()
        for (currencyConversion in currencyConversions.values) {
            contentText.append("${CurrencySymbolConverter.getSymbol(currencyConversion.srcCurrency)} ${
                "%.2f".format(
                    currencyConversion.srcAmount
                )
            } -> ${
                CurrencySymbolConverter.getSymbol(currencyConversion.trgtCurrency)
            } ${
                if (currencyConversion.conversionPrice !== 0F) {
                    "%.2f".format(
                        currencyConversion.conversionPrice * currencyConversion.srcAmount
                    )
                } else {
                    "~.~~"
                }
            }  (${
                CurrencySymbolConverter.getSymbol(currencyConversion.trgtCurrency)
            } ${
                if (currencyConversion.conversionPrice !== 0F) {
                    "%.2f".format(
                        currencyConversion.conversionPrice * currencyConversion.srcAmount -
                                currencyConversion.srcPrice * currencyConversion.srcAmount
                    )
                } else {
                    "..."
                }
            } ${
                if (
                    (
                            currencyConversion.conversionPrice * currencyConversion.srcAmount -
                                    currencyConversion.srcPrice * currencyConversion.srcAmount
                            ) > 0
                ) {
                    "win"
                } else {
                    "loss"
                }
            })\n")
        }
        return contentText.toString().trim()
    }

    private fun createNotificationBuilder(): NotificationCompat.Builder {
        // Create a notification channel if needed (for Android 8+)
        createNotificationChannel()

        // Build a notification for the foreground service
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//            .setContentTitle("Money Pots")
            .setSmallIcon(R.drawable.baseline_auto_graph_24)
    }
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        var contentText = ""
//        val currencyConversions = fetchStoredAmounts(this)
//        for (currencyConversion in currencyConversions) {
//            contentText += "${CurrencySymbolConverter.getSymbol(currencyConversion.value.srcCurrency)} ${
//                "%.2f".format(
//                    currencyConversion.value.srcAmount
//                )
//            } -> ${
//                CurrencySymbolConverter.getSymbol(currencyConversion.value.trgtCurrency)
//            } ${
//                if (currencyConversion.value.conversionPrice !== 0F) {
//                    "%.2f".format(
//                        currencyConversion.value.conversionPrice * currencyConversion.value.srcAmount
//                    )
//                } else {
//                    "~.~~"
//                }
//            }  (${
//                CurrencySymbolConverter.getSymbol(currencyConversion.value.trgtCurrency)
//            } ${
//                if (currencyConversion.value.conversionPrice !== 0F) {
//                    "%.2f".format(
//                        currencyConversion.value.conversionPrice * currencyConversion.value.srcAmount -
//                                currencyConversion.value.srcPrice * currencyConversion.value.srcAmount
//                    )
//                } else {
//                    "..."
//                }
//            } ${
//                if (
//                    (
//                        currencyConversion.value.conversionPrice * currencyConversion.value.srcAmount -
//                                currencyConversion.value.srcPrice * currencyConversion.value.srcAmount
//                    ) > 0
//                ) {
//                    "win"
//                } else {
//                    "loss"
//                }
//            })\n"
//        }
//        // Create a notification channel if needed (for Android 8+)
//        createNotificationChannel()
//
//        // Build a notification for the foreground service
//        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
////            .setContentTitle("Money Pots")
//            .setContentText(contentText)
//            .setSmallIcon(R.drawable.baseline_auto_graph_24)
//            .build()
//
//        // Start the service in the foreground with the notification
//        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
//
//        // Perform your service logic here
//
//        // Return START_STICKY to indicate that the service should be restarted if terminated
//        return START_STICKY
//    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "MoneyPotsChannel",
                NotificationManager.IMPORTANCE_HIGH
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
