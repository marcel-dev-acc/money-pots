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
import com.marcelm.myfinances.ui.methods.CurrencyConversion
import com.marcelm.myfinances.ui.methods.currencyConversions
import com.marcelm.myfinances.ui.methods.deleteFromAmounts
import com.marcelm.myfinances.ui.methods.fetchCurrencyPair
import com.marcelm.myfinances.ui.methods.fetchStoredAmounts
import com.marcelm.myfinances.ui.methods.storeAmounts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyFinancesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFF000024)
                ) {
                    Home(context = LocalContext.current)
                }
            }
        }
    }
}

val gradientColors = listOf(
    Color(0xFF3DED97),
    Color(0xFF03AC13),
    Color(0xFFB0FC38)
)

@Composable
fun Home(context: Context, modifier: Modifier = Modifier, propShowModal: Boolean = false) {

    var showModal by remember {
        mutableStateOf(propShowModal)
    }

    // Check if amounts contains any values
    if (currencyConversions.size === 0) {
        fetchStoredAmounts(context)
        for (currencyConversion in currencyConversions) {
            fetchCurrencyPair(
                context = context,
                srcCurrency = currencyConversion.srcCurrency,
                trgtCurrency = currencyConversion.trgtCurrency,
            )
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
                LazyColumn () {
                    items(currencyConversions.size) { idx ->
                        AmountRow(
                            index = idx,
                            currencyConversion = currencyConversions[idx],
                            onDelete = { idx ->
                                deleteFromAmounts(context, idx)
                            }
                        )
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
                    storeAmounts(context, currencyConversion)
                    showModal = false
                }
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun HomePreview() {
    currencyConversions.add(
        CurrencyConversion(
            srcCurrency = "EUR",
            srcAmount = 10.10F,
            srcPrice = 0.86F,
            trgtCurrency = "GBP",
            transactionTime = 1691928664,
            conversionPrice = 0F
        )
    )
    MyFinancesTheme {
        Home(
            context = LocalContext.current,
            propShowModal = true
        )
    }
}