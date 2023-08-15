package com.marcelm.myfinances.ui.methods

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.core.content.edit

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

val currencyConversions = mutableStateListOf<CurrencyConversion>()

fun loadAmountsFromPreferences(context: Context): List<CurrencyConversion> {
    val sharedPreferences = context.getSharedPreferences(sharedPrefStorageKey, Context.MODE_PRIVATE)
    val currencyConversionsString = sharedPreferences.getString(storageKey, "")
    if (currencyConversionsString === "") {
        return emptyList()
    }
    val currencyConversionsListString =
        currencyConversionsString?.split(",")?.map { it }
    val currencyConversion = mutableStateListOf<CurrencyConversion>()
    if (currencyConversionsListString != null) {
        for (currencyConversionString in currencyConversionsListString) {
            val currencyConversionListString =
                currencyConversionString?.split(";")?.map { it }
            currencyConversion.add(
                CurrencyConversion(
                    srcCurrency = currencyConversionListString?.get(0) ?: "",
                    srcAmount = currencyConversionListString?.get(1)?.toFloat() ?: 0F,
                    srcPrice = currencyConversionListString?.get(2)?.toFloat() ?: 0F,
                    trgtCurrency = currencyConversionListString?.get(3) ?: "",
                    transactionTime = currencyConversionListString?.get(4)?.toLong() ?: 0,
                    conversionPrice = currencyConversionListString?.get(5)?.toFloat() ?: 0F,
                ))
        }
    }
    return currencyConversion
}


fun fetchStoredAmounts(context: Context) {
    val internalAmounts = loadAmountsFromPreferences(context)
    for (amount in internalAmounts) {
        currencyConversions.add(amount)
    }
}

fun saveAmountsToPreferences(context: Context, currencyConversions: List<CurrencyConversion>) {
    val currencyConversionStrRep = mutableStateListOf<String>()
    for (currencyConversion in currencyConversions) {
        currencyConversionStrRep.add(
            currencyConversion.srcCurrency + ";" +
            currencyConversion.srcAmount.toString() + ";" +
            currencyConversion.srcPrice.toString() + ";" +
            currencyConversion.trgtCurrency + ";" +
            currencyConversion.transactionTime + ";" +
            currencyConversion.conversionPrice
        )
    }
    val sharedPreferences = context.getSharedPreferences(sharedPrefStorageKey, Context.MODE_PRIVATE)
    sharedPreferences.edit {
        putString(storageKey, currencyConversionStrRep.joinToString(","))
        apply()
    }
}

fun storeAmounts(context: Context, currencyConversion: CurrencyConversion) {
    currencyConversions.add(currencyConversion)
    saveAmountsToPreferences(context, currencyConversions)
}

fun deleteFromAmounts(context: Context, index: Int) {
    if (index in 0 until currencyConversions.size) {
        currencyConversions.removeAt(index)
        saveAmountsToPreferences(context, currencyConversions)
    }
}
