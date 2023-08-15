package com.marcelm.myfinances

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.Preview
import com.marcelm.myfinances.ui.components.*
import com.marcelm.myfinances.ui.theme.MyFinancesTheme
import android.content.Context
import androidx.compose.ui.platform.LocalContext
import com.marcelm.myfinances.ui.methods.*

class MainActivity : ComponentActivity(), GlobalCurrencyConversionChangeListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Add the MainActivity as a listener to global currency conversions changes
        GlobalCurrencyConversionManager.addListener(this@MainActivity)
        // Set the content of the screen
        setContent {
            MyFinancesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF000024)
                ) {
                    Home(context = LocalContext.current, modifier = Modifier)
                }
            }
        }
    }

    override fun onGlobalCurrencyConversionsChanged(newCurrencyConversions: Map<String, CurrencyConversion>) {
        if (newCurrencyConversions.isEmpty()) {
            globalCurrencyConversionsVariable.clear()
        } else {
            globalCurrencyConversionsVariable.clear()
            globalCurrencyConversionsVariable.putAll(newCurrencyConversions)
        }
    }
}

val gradientColors = listOf(
    Color(0xFF3DED97),
    Color(0xFF03AC13),
    Color(0xFFB0FC38)
)

@Composable
fun Home(
    context: Context,
    modifier: Modifier = Modifier,
    propShowModal: Boolean = false,
) {
    var showModal by remember {
        mutableStateOf(propShowModal)
    }

    if (globalCurrencyConversionsVariable.isEmpty()) {
        val currencyConversions = fetchStoredAmounts(context = context)
        globalCurrencyConversionsVariable.putAll(currencyConversions)
    }

    if (globalCurrencyConversionsVariable.isNotEmpty()) {
        for (currencyConversion in globalCurrencyConversionsVariable) {
            if (currencyConversion.value.conversionPrice == 0F) {
                fetchCurrencyPair(context = context, currencyConversion = currencyConversion)
            }
        }
    }

    Box (
        modifier = modifier
            .fillMaxSize(),
    ) {
        Row (
            modifier = modifier
                    .fillMaxWidth()
        ) {
            Column(
                modifier = modifier
                    .fillMaxWidth()
            ) {
                Header(modifier)
                // Euro to gbp amount section
                LazyColumn {
                    items(globalCurrencyConversionsVariable.keys.toList().size) { idx ->
                        val id = globalCurrencyConversionsVariable.keys.toList()[idx]
                        val currencyConversion = globalCurrencyConversionsVariable[id]
                        if (currencyConversion != null) {
                            AmountRow(
                                id = id,
                                currencyConversion = currencyConversion,
                                onDelete = { id ->
                                    deleteAmount(context, id)
                                }
                            )
                        }
                    }
                }
            }
        }
        AddToPotButton(
            modifier.align(Alignment.BottomEnd),
            onSubmit = { run {
                showModal = !showModal
            } }
        )
        if (showModal) {
            AddToPotModal(
                context = context,
                modifier.align(Alignment.Center),
                onSubmit = { currencyConversion: CurrencyConversion ->
                    storeAmount(context, currencyConversion)
                    showModal = false
                }
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun HomePreview() {
    val id = "47c6977b-e8a3-416e-9c8b-8512314871b8"
    globalCurrencyConversionsVariable[id] = CurrencyConversion(
        srcCurrency = "EUR",
        srcAmount = 10.10F,
        srcPrice = 0.86F,
        trgtCurrency = "GBP",
        transactionTime = 1691928664,
        conversionPrice = 0F
    )
    MyFinancesTheme {
        Home(
            context = LocalContext.current,
            propShowModal = true,
        )
    }
}