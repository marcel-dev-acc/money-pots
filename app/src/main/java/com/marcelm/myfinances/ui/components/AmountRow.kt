package com.marcelm.myfinances.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcelm.myfinances.gradientColors
import com.marcelm.myfinances.ui.methods.CurrencyConversion
import com.marcelm.myfinances.ui.methods.CurrencySymbolConverter
import com.marcelm.myfinances.ui.methods.convertEpochTimeMsToDate

@Composable
fun AmountRow(
    modifier: Modifier = Modifier,
    index: Int,
    currencyConversion: CurrencyConversion,
    onDelete: (idx: Int) -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = 10.dp,
                    horizontal = 10.dp,
                )
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors
                    ),
                    shape = RoundedCornerShape(4.dp),
                )
                .shadow(
                    elevation = 5.dp,
                    spotColor = Color.White,
                    ambientColor = Color.White,
                )
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(2.dp)
                    .background(Color(0xFF000024)),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(
                            start = 10.dp,
                        ),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier = modifier,
                        text = convertEpochTimeMsToDate(currencyConversion.transactionTime),
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = Color(0xFFF5F5F5),
                        )
                    )
                    Text(
                        modifier = modifier,
                        text = "${CurrencySymbolConverter.getSymbol(currencyConversion.srcCurrency)} ${"%.2f".format(
                            currencyConversion.srcAmount
                        )}",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color(0xFFF5F5F5),
                        )
                    )
                    Text(
                        modifier = modifier,
                        text = if (currencyConversion.conversionPrice === 0F) {
                            "Fetching..."
                        } else {
                            "%.5f".format(
                                currencyConversion.conversionPrice
                            )
                        },
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = Color(0xFFF5F5F5),
                        )
                    )
                }
                Column(
                    modifier = modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier = modifier,
                        text = "${currencyConversion.srcPrice}",
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = Color(0xFFF5F5F5),
                        )
                    )
                    Text(
                        modifier = modifier,
                        text = "${CurrencySymbolConverter.getSymbol(currencyConversion.trgtCurrency)} ${
                            if (currencyConversion.conversionPrice !== 0F) {
                                "%.2f".format(
                                    currencyConversion.conversionPrice * currencyConversion.srcAmount
                                )
                            } else {
                                "~.~~"
                            }
                        }",
                        style = TextStyle(
                            fontSize = 20.sp,
                            color = Color(0xFFF5F5F5),
                        )
                    )
                    Text(
                        modifier = modifier,
                        text = if (currencyConversion.conversionPrice !== 0F) {
                            "%.2f".format(
                                currencyConversion.conversionPrice * currencyConversion.srcAmount -
                                        currencyConversion.srcPrice * currencyConversion.srcAmount
                            )
                        } else {
                            "..."
                        },
                        style = TextStyle(
                            fontSize = 10.sp,
                            color = Color(0xFFF5F5F5),
                        )
                    )
                }
                Column(
                    modifier = modifier
                        .fillMaxHeight()
                        .padding(
                            end = 10.dp
                        ),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        modifier = modifier
                            .size(30.dp)
                            .clickable {
                                onDelete(index)
                            },
                        imageVector = Icons.Default.Delete, contentDescription = "Delete",
                        tint = Color(0xFFF5F5F5),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = false)
@Composable
fun AmountRowPreview() {
    AmountRow(
        index = 1,
        currencyConversion = CurrencyConversion(
            srcCurrency = "EUR",
            srcAmount = 1234.12F,
            srcPrice = 0.86F,
            trgtCurrency = "GBP",
            transactionTime = 1691928664,
            conversionPrice = 0F
        ),
        onDelete = {}
    )
}