package com.marcelm.myfinances.ui.methods

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.net.ssl.SSLHandshakeException
import org.jsoup.Jsoup

import androidx.work.CoroutineWorker
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

object CurrencySymbolConverter {
    private val currencySymbolMap = mapOf(
        "GBP" to "£",
        "EUR" to "€",
        "ZAR" to "R"
        // Add more currency codes and symbols as needed
    )

    fun getSymbol(currencyCode: String): String {
        return currencySymbolMap[currencyCode] ?: ""
    }
}


fun fetchCurrencyPair(context: Context, srcCurrency: String, trgtCurrency: String) {
    GlobalScope.launch(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://finance.yahoo.com/quote/${srcCurrency}${trgtCurrency}=X/")
            .addHeader("Content-Type", "application/json")
            .build()

        try {
            val response: Response = client.newCall(request).execute()
            val responseBody = response.body?.string()
            if (responseBody !== null) {
                val price = extractInnerText(
                    responseBody.toString(),
                    "${srcCurrency}${trgtCurrency}=X"
                )
                try {
                    val numPrice = price?.toFloat()
                    // Create new display currencyConversions list
                    val newCurrencyConversions = mutableStateListOf<CurrencyConversion>()
                    for (currencyConversion in currencyConversions) {
                        if (
                            currencyConversion.srcCurrency === srcCurrency &&
                            currencyConversion.trgtCurrency === trgtCurrency
                        ) {
                            val newCurr = CurrencyConversion(
                                srcCurrency = currencyConversion.srcCurrency,
                                srcAmount = currencyConversion.srcAmount,
                                srcPrice = currencyConversion.srcPrice,
                                trgtCurrency = currencyConversion.trgtCurrency,
                                transactionTime = currencyConversion.transactionTime,
                                conversionPrice = if (numPrice !== null) numPrice else 0F,
                            )
                            newCurrencyConversions.add(newCurr)
                        } else {
                            newCurrencyConversions.add(currencyConversion)
                        }
                    }
                    // Delete all existing currencyConversions
                    for (idx in 0 until currencyConversions.size) {
                        deleteFromAmounts(context, idx)
                    }
                    // Populate currencyConversions with new list
                    for (newCurrencyConversion in newCurrencyConversions) {
                        storeAmounts(context, newCurrencyConversion)
                    }
                } catch (e: NumberFormatException) {
                    // Do nothing
                }
            }
        } catch (e: SSLHandshakeException) {
            // Handle SSLHandshakeException
            Log.d("fetchCurrencyPairError", "Device time might be set incorrectly")
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

fun extractInnerText(htmlBody: String, targetSymbol: String): String? {
    val document = Jsoup.parse(htmlBody)
    val finStreamers = document.select("fin-streamer[data-symbol=\"$targetSymbol\"]")

    for (finStreamer in finStreamers) {
        val innerText = finStreamer.ownText()
        if (innerText.isNotEmpty()) {
            return innerText
        }
    }
    return null
}
