package com.marcelm.myfinances.ui.methods

import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.media.RingtoneManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.RemoteViews
import androidx.compose.ui.platform.ComposeView
import com.marcelm.myfinances.R
import java.util.Timer

class MyForegroundService : Service(), GlobalCurrencyConversionChangeListener {
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val handler = Handler(Looper.getMainLooper())
    private val fetchCurrencyPairRunnable = object : Runnable {
        override fun run() {
            val currencyConversions = fetchStoredAmounts(this@MyForegroundService)
            // Call fetchCurrencyPair() for each stored currency pair
            for (currencyConversion in currencyConversions) {
                fetchCurrencyPair(this@MyForegroundService, currencyConversion)
            }

            // Schedule the runnable to run again after a minute
            handler.postDelayed(this, 60 * 1000) // 60 seconds * 1000 milliseconds
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val currencyConversions = fetchStoredAmounts(this)
        val contentText = buildNotificationContent(currencyConversions)

        // Initialize the notification manager and builder
        notificationManager = getSystemService(NotificationManager::class.java)
        notificationBuilder = createNotificationBuilder()
//        notificationBuilder.setContentText(contentText)
        // Inflate the custom notification layout
//        val remoteViews = RemoteViews(packageName, R.layout.notification_custom_layout)
//        remoteViews.setTextViewText(R.id.contentTextView, contentText)
//        notificationBuilder.setCustomContentView(remoteViews)
//        notificationBuilder.setContent(remoteViews)
        notificationBuilder.setContentText(contentText)

        // Start the service in the foreground with the initial notification
        startForeground(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build())

        // Add the MyForegroundService as a listener to global currency conversions changes
        GlobalCurrencyConversionManager.addListener(this)

        // Schedule the fetchCurrencyPairRunnable to run immediately and then periodically
        handler.post(fetchCurrencyPairRunnable)

        // Return START_STICKY to indicate that the service should be restarted if terminated
        return START_STICKY
    }

    override fun onGlobalCurrencyConversionsChanged(newCurrencyConversions: Map<String, CurrencyConversion>) {
        Log.d("MyForegroundService", "new content ${newCurrencyConversions.toString()}")

        // Update the notification content based on the new currency conversions
        val contentText = buildNotificationContent(newCurrencyConversions)

        // Inflate the custom notification layout
//        val remoteViews = RemoteViews(packageName, R.layout.notification_custom_layout)
//        remoteViews.setTextViewText(R.id.contentTextView, contentText)
//        notificationBuilder.setCustomContentView(remoteViews)
//        notificationBuilder.setContent(remoteViews)
        notificationBuilder.setContentText(contentText)

        // Notify the notification manager to update the notification
        notificationManager.notify(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun buildNotificationContent(currencyConversions: Map<String, CurrencyConversion>): String {
        val contentText = StringBuilder()
        var playSound = false
        var recorded = 0
        var itr = 0
        val objectLength = currencyConversions.keys.toList().size
        for (currencyConversion in currencyConversions.values) {
            if (
                (
                    (currencyConversion.conversionPrice - currencyConversion.srcPrice) /  currencyConversion.srcPrice
                ) > 0.05F
            ) {
                playSound = true
                recorded += 1
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
            } else if (
                (recorded < 2 && itr == objectLength - 2) || (recorded < 2 && itr == objectLength - 1)
            ) {
                recorded += 1
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
            itr += 1
        }
        if (playSound) {
            val defaultNotificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationRingtone = RingtoneManager.getRingtone(applicationContext, defaultNotificationSound)
            notificationRingtone.play()
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
            .setNotificationSilent()
    }

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

    override fun onDestroy() {
        // Remove the fetchCurrencyPairRunnable callbacks from the handler
        handler.removeCallbacks(fetchCurrencyPairRunnable)

        // Stop the service
        stopSelf()

        super.onDestroy()
    }

    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "MyForegroundServiceChannel"
        private const val FOREGROUND_NOTIFICATION_ID = 1
    }
}
