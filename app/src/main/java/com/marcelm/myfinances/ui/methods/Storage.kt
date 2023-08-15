package com.marcelm.myfinances.ui.methods

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.core.content.edit
import java.util.*

const val sharedPrefStorageKey: String = "MyFinancesPrefs"
const val storageKey: String = "currencyConversions"

val globalCurrencyConversionsVariable = mutableStateMapOf<String, CurrencyConversion>()

data class CurrencyConversion(
    val srcCurrency: String,
    val srcAmount: Float,
    val srcPrice: Float,
    val trgtCurrency: String,
    val transactionTime: Long,
    val conversionPrice: Float,
)

interface GlobalCurrencyConversionChangeListener {
    fun onGlobalCurrencyConversionsChanged(newValues: Map<String, CurrencyConversion>)
}

object GlobalCurrencyConversionManager {
    private val listeners = mutableListOf<GlobalCurrencyConversionChangeListener>()
    private var currencyConversions: MutableMap<String, CurrencyConversion> = mutableMapOf()

    fun setGlobalCurrencyConversions(newValue: Map<String, CurrencyConversion>) {
        currencyConversions.putAll(newValue)
        notifyListeners(currencyConversions)
    }

    fun getGlobalCurrencyConversions(): Map<String, CurrencyConversion> {
        return currencyConversions
    }

    fun removeFromGlobalCurrencyConversion(id: String) {
        currencyConversions.remove(id)
        notifyListeners(currencyConversions)
    }
    fun clearGlobalCurrencyConversions() {
        currencyConversions.clear()
        notifyListeners(emptyMap())
    }

    fun addListener(listener: GlobalCurrencyConversionChangeListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: GlobalCurrencyConversionChangeListener) {
        listeners.remove(listener)
    }

    private fun notifyListeners(newValues: Map<String, CurrencyConversion>) {
        listeners.forEach { it.onGlobalCurrencyConversionsChanged(newValues) }
    }
}

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
        if (currencyConversionParts.size == 7) {
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

fun fetchStoredAmounts(context: Context): Map<String, CurrencyConversion> {
    val internalAmounts = loadAmountsFromPreferences(context)
    GlobalCurrencyConversionManager.clearGlobalCurrencyConversions()
    GlobalCurrencyConversionManager.setGlobalCurrencyConversions(internalAmounts)
    return GlobalCurrencyConversionManager.getGlobalCurrencyConversions()
}

fun saveAmountsToPreferences(context: Context, currencyConversions: MutableMap<String, CurrencyConversion>) {
    var currencyConversionStrRep = mutableListOf<String>()
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
    if (storeId == null) {
        storeId = UUID.randomUUID().toString()
    }
    // Create a new map to store data
    var currencyConversions: MutableMap<String, CurrencyConversion> = mutableMapOf()
    // Set the global currency conversion here
    currencyConversions.putAll(GlobalCurrencyConversionManager.getGlobalCurrencyConversions())
    // Adjust for incoming data
    currencyConversions[storeId] = currencyConversion
    // Change the global currency conversions map
    GlobalCurrencyConversionManager.setGlobalCurrencyConversions(currencyConversions)
    // Store locally
    saveAmountsToPreferences(context, currencyConversions)
}

fun deleteAmount(context: Context, id: String) {
    // Create a new map to store data
    var currencyConversions: MutableMap<String, CurrencyConversion> = mutableMapOf()
    // Set the global currency conversion here
    currencyConversions.putAll(GlobalCurrencyConversionManager.getGlobalCurrencyConversions())
    // Remove the specific id
    currencyConversions.remove(id)
    // Remove from global currency conversions map
    GlobalCurrencyConversionManager.removeFromGlobalCurrencyConversion(id)
    // Store locally
    saveAmountsToPreferences(context, currencyConversions)
}
