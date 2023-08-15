package com.marcelm.myfinances.ui.methods

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import javax.net.ssl.SSLHandshakeException
import org.jsoup.Jsoup

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


fun fetchCurrencyPair(context: Context, currencyConversion: MutableMap.MutableEntry<String, CurrencyConversion>) {
    GlobalScope.launch(Dispatchers.IO) {
        val srcCurrency = currencyConversion.value.srcCurrency
        val trgtCurrency = currencyConversion.value.trgtCurrency
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
                    if (numPrice !== null) {
                        storeAmount(
                            context,
                            CurrencyConversion(
                                srcCurrency = currencyConversion.value.srcCurrency,
                                srcAmount = currencyConversion.value.srcAmount,
                                srcPrice = currencyConversion.value.srcPrice,
                                trgtCurrency = currencyConversion.value.trgtCurrency,
                                transactionTime = currencyConversion.value.transactionTime,
                                conversionPrice = numPrice,
                            ),
                            currencyConversion.key,
                        )
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
