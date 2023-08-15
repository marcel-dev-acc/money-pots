package com.marcelm.myfinances.ui.components

import android.content.Context
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marcelm.myfinances.gradientColors
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import com.marcelm.myfinances.ui.methods.CurrencyConversion
import android.app.DatePickerDialog
import com.marcelm.myfinances.ui.methods.convertDateToEpochTimeMs
import java.util.*

@OptIn(ExperimentalTextApi::class)
@Composable
fun AddToPotModal(
    context: Context,
    modifier: Modifier = Modifier,
    onSubmit: (CurrencyConversion) -> Unit
) {
    val currencies = listOf("GBP", "EUR", "ZAR")
    val sourceStr = "Source "
    val targetStr = "Target "

    var transactionAmount by remember {
        mutableStateOf("0.00")
    }
    var transactionPrice by remember {
        mutableStateOf("0.0")
    }
    var srcCurrencyDropdown by remember {
        mutableStateOf(false)
    }
    val selectedSrcCurrencyDefault = "Select source currency"
    var selectedSrcCurrency by remember {
        mutableStateOf(selectedSrcCurrencyDefault)
    }
    var trgtCurrencyDropdown by remember {
        mutableStateOf(false)
    }
    val selectedTrgtCurrencyDefault = "Select target currency"
    var selectedTrgtCurrency by remember {
        mutableStateOf(selectedTrgtCurrencyDefault)
    }
    // Date picker vars
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()
    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)
    mCalendar.time = Date()
    // Declaring a string value to store date in string format
    val mDate = remember { mutableStateOf("") }
    val mDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "${if (mDayOfMonth < 10) "0${mDayOfMonth}" else mDayOfMonth.toString()}/${if (mMonth+1 < 10) "0${mMonth+1}" else (mMonth+1).toString()}/$mYear"
        }, mYear, mMonth, mDay
    )

    // Modal
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                ),
                shape = RoundedCornerShape(4.dp),
            )
            .padding(2.dp)
            .width((LocalConfiguration.current.screenWidthDp * 0.9).dp)
            .shadow(
                elevation = 5.dp,
                spotColor = Color.White,
                ambientColor = Color.White,
            ),
    ) {
        Column(
            modifier = modifier
                .background(Color(0xFF000024))
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row (
                modifier = modifier
                    .fillMaxWidth(),
            ) {
                Column(
                    modifier = modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Add to pot",
                        style = TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 50.sp,
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                    )
                }
            }
            Spacer(modifier = modifier.height(16.dp))
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = modifier
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Source currency selector
                    Text(
                        modifier = modifier
                            .clickable {
                                run {
                                    srcCurrencyDropdown = true
                                }
                            },
                        text = selectedSrcCurrency,
                        style = TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 20.sp,
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                    )
                    DropdownMenu(
                        modifier = modifier,
                        offset = DpOffset(
                            y = (-160).dp,
                            x = 120.dp,
                        ),
                        expanded = srcCurrencyDropdown,
                        onDismissRequest = { }
                    ) {
                        currencies.forEach { currency ->
                            DropdownMenuItem(
                                onClick = { run {
                                    selectedSrcCurrency = "$sourceStr$currency"
                                    srcCurrencyDropdown = false
                                } },
                                text = {
                                    Text(text = currency)
                                }
                            )
                        }
                    }
                    Spacer(modifier = modifier.height(16.dp))
                    // Target currency selector
                    Text(
                        modifier = modifier
                            .clickable {
                                run {
                                    trgtCurrencyDropdown = true
                                }
                            },
                        text = selectedTrgtCurrency,
                        style = TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 20.sp,
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                    )
                    DropdownMenu(
                        modifier = modifier,
                        offset = DpOffset(
                            y = (-120).dp,
                            x = 120.dp,
                        ),
                        expanded = trgtCurrencyDropdown,
                        onDismissRequest = { }
                    ) {
                        currencies.forEach { currency ->
                            DropdownMenuItem(
                                onClick = { run {
                                    selectedTrgtCurrency = "$targetStr$currency"
                                    trgtCurrencyDropdown = false
                                } },
                                text = {
                                    Text(text = currency)
                                }
                            )
                        }
                    }
                    Spacer(modifier = modifier.height(16.dp))
                    // Value input
                    BasicTextField(
                        modifier = modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF5F5F5))
                            .padding(vertical = 5.dp),
                        value = transactionAmount,
                        onValueChange = { transactionAmount = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            color = Color(0xFFF5F5F5),
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                        ),
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    // Date picker module
                    Text(
                        modifier = modifier,
                        text = "Transaction date",
                        style = TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 20.sp,
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                    )
                    Text(
                        modifier = modifier
                            .clickable {
                                mDatePickerDialog.show()
                            },
                        text = if (mDate.value.isNotEmpty()) "${mDate.value}" else "--/--/----",
                        style = TextStyle(
                            fontFamily = FontFamily.Cursive,
                            fontSize = 20.sp,
                            brush = Brush.linearGradient(
                                colors = gradientColors
                            )
                        ),
                    )
                    Spacer(modifier = modifier.height(16.dp))
                    // Day price
                    BasicTextField(
                        modifier = modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFF5F5F5))
                            .padding(vertical = 5.dp),
                        value = transactionPrice,
                        onValueChange = { transactionPrice = it },
                        keyboardOptions = KeyboardOptions.Default.copy(
                            keyboardType = KeyboardType.Number
                        ),
                        textStyle = TextStyle(
                            color = Color(0xFFF5F5F5),
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                        ),
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = modifier
                    .padding(16.dp)
                    .padding(
                        horizontal = 10.dp,
                    )
                    .background(
                        brush = Brush.linearGradient(
                            colors = gradientColors
                        ),
                        shape = RoundedCornerShape(4.dp),
                    )
                    .clickable {
                        if (selectedSrcCurrency !== selectedSrcCurrencyDefault &&
                            selectedTrgtCurrency !== selectedTrgtCurrencyDefault &&
                            transactionAmount.toFloat() > 0F &&
                            transactionPrice.toFloat() > 0F &&
                            mDate.value.isNotEmpty()
                        ) {
                            onSubmit(
                                CurrencyConversion(
                                    srcCurrency = selectedSrcCurrency.replace(
                                        sourceStr, ""
                                    ),
                                    srcAmount = transactionAmount.toFloat(),
                                    srcPrice = transactionPrice.toFloat(),
                                    trgtCurrency = selectedTrgtCurrency.replace(
                                        targetStr, ""
                                    ),
                                    transactionTime = if (convertDateToEpochTimeMs(mDate.value) > 0) convertDateToEpochTimeMs(mDate.value) / 1000 else 0,
                                    conversionPrice = 0F,
                                )
                            )
                        } else {
                            Toast.makeText(
                                context,
                            "Please select source, target currency, set an amount and transaction date",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
            ) {
                Text(
                    text = "Submit",
                    modifier = modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 5.dp,
                        ),
                    fontSize = 30.sp,
                    color = Color(0xFFF5F5F5),
                )
            }
        }
    }
}


@Preview(showBackground = false)
@Composable
fun AddToPotModalPreview() {
    Box {
        AddToPotModal(context = LocalContext.current, onSubmit = {})
        AddToPotButton(onSubmit = {})
    }
}

@Composable
fun AddToPotButton(modifier: Modifier = Modifier, onSubmit: () -> Unit) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .padding(
                horizontal = 10.dp,
            )
            .background(
                brush = Brush.linearGradient(
                    colors = gradientColors
                ),
                shape = RoundedCornerShape(4.dp),
            )
            .clickable {
                onSubmit()
            },
    ) {
        Icon(
            imageVector = Icons.Default.Add, contentDescription = "Add",
            tint = Color(0xFFF5F5F5),
            modifier = modifier
                .size(40.dp),
        )
    }
}
