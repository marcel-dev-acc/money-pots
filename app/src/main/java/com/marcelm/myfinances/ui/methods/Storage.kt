package com.marcelm.myfinances.ui.methods

import android.content.Context
import androidx.core.content.edit
import java.util.*

const val sharedPrefStorageKey: String = "MyFinancesPrefs"
const val storageKey: String = "currencyConversions"

data class CurrencyConversion(
    val srcCurrency: String,
    val srcAmount: Float,
    val srcPrice: Float,
    val trgtCurrency: String,
    val transactionTime: Long,
    val conversionPrice: Float,
)

val currencyConversions: MutableMap<String, CurrencyConversion> = mutableMapOf()

fun loadAmountsFromPreferences(context: Context): Map<String, CurrencyConversion> {
    val sharedPreferences = context.getSharedPreferences(sharedPrefStorageKey, Context.MODE_PRIVATE)
    val currencyConversionsString = sharedPreferences.getString(storageKey, "")
    if (currencyConversionsString.isNullOrEmpty()) {
        return emptyMap()
    }
    val currencyConversionMap = mutableMapOf<String, CurrencyConversion>()
    val currencyConversionsList = currencyConversionsString.split(",")
    for (currencyConversionString in currencyConversionsList) {
        val currencyConversionParts = currencyConversionString.split(";")
        if (currencyConversionParts.size == 6) {
            val id = currencyConversionParts[0]
            currencyConversionMap[id] = CurrencyConversion(
                srcCurrency = currencyConversionParts[1],
                srcAmount = currencyConversionParts[2].toFloat(),
                srcPrice = currencyConversionParts[3].toFloat(),
                trgtCurrency = currencyConversionParts[4],
                transactionTime = currencyConversionParts[5].toLong(),
                conversionPrice = currencyConversionParts[6].toFloat()
            )
        }
    }
    return currencyConversionMap
}

fun fetchStoredAmounts(context: Context) {
    val internalAmounts = loadAmountsFromPreferences(context)
    currencyConversions.clear()
    currencyConversions.putAll(internalAmounts)
}

fun saveAmountsToPreferences(context: Context) {
    val currencyConversionStrRep = mutableListOf<String>()
    for ((id, currencyConversion) in currencyConversions) {
        currencyConversionStrRep.add(
            "$id;" +
                    "${currencyConversion.srcCurrency};" +
                    "${currencyConversion.srcAmount};" +
                    "${currencyConversion.srcPrice};" +
                    "${currencyConversion.trgtCurrency};" +
                    "${currencyConversion.transactionTime};" +
                    "${currencyConversion.conversionPrice}"
        )
    }
    val sharedPreferences = context.getSharedPreferences(sharedPrefStorageKey, Context.MODE_PRIVATE)
    sharedPreferences.edit {
        putString(storageKey, currencyConversionStrRep.joinToString(","))
        apply()
    }
}

fun storeAmount(context: Context, currencyConversion: CurrencyConversion, id: String? = null) {
    var storeId = id
    if (storeId === null) {
        storeId = UUID.randomUUID().toString()
    }
    currencyConversions[storeId] = currencyConversion
    saveAmountsToPreferences(context)
}

fun deleteAmount(context: Context, id: String) {
    currencyConversions.remove(id)
    saveAmountsToPreferences(context)
}
