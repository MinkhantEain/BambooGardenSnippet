package com.example.bamboogarden.menu.menuHistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.R
import com.example.bamboogarden.common.TextBoxDateSelector.CustomDatePicker
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.common.viewModelFactory
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuHistoryScreen(modifier: Modifier = Modifier,
                      onBackClick: () -> Unit) {
    val viewModel: MenuHistoryScreenViewModel =
        viewModel(factory = viewModelFactory { MenuHistoryScreenViewModel() })

    val showDatePicker by remember { viewModel.showDialog }
    val date by remember { viewModel.date }
    val payments by remember { viewModel.payments }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Menu Bill History") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            contentDescription = "Back Button",
                            painter = painterResource(id = R.drawable.back_arrow),
                            tint = Color(1,127,161,255),
                        )
                    }
                }
            )
        },
    ) { padding ->
        Box {
            if (showDatePicker)
                CustomDatePicker(
                    givenDate = date,
                    onDismiss = { viewModel.showDialog.value = false },
                    onConfirm = { localDate ->
                        viewModel.date.value = localDate
                        viewModel.changeDate(localDate)
                    }
                )
            LazyColumn(contentPadding = padding) {
                val historyValueFontSize = TextUnit(25f, TextUnitType.Sp)
                val historyValueColor = Color(78, 79, 78, 255)
                item(key = "DatePicker") {
                    TextField(
                        value = date.format(DateTimeFormatter.ofPattern("dd/MMM/yyyy")),
                        onValueChange = {},
                        modifier =
                        Modifier
                            .fillParentMaxWidth()
                            .clickable {
                                viewModel.showDialog.value = true
                            },
                        maxLines = 1,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.DateRange,
                                contentDescription = "Date Icon"
                            )
                        },
                        enabled = false,
                    )
                }

                items(count = payments.size) { index ->
                    val payment = payments[index]
                    Row(
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = "${payment.tableId}${if (payment.tax != 0) " Tax" else ""}${if (payment.serviceCharge != 0) " SC" else ""}")
                        Text(text = payment.time)
                    }
                    payment.wrappers.forEach {
                        Row(horizontalArrangement = Arrangement.SpaceBetween,
                          modifier = Modifier
                              .fillMaxWidth()
                              .padding(horizontal = 10.dp)) {
                            Text(text = it.dish.name)
                            Text(text = it.count.toString())
                            Text(text = it.totalCost.toCurrency())
                        }
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween,
                      modifier = Modifier
                          .fillMaxSize()
                          .padding(horizontal = 10.dp)) {
                        Text(text = "Total")
                        Text(text = payment.totalCost.toCurrency())
                    }
                  HorizontalDivider(thickness = 1.dp, color = Color.Black)
                }
            }
        }
    }
}
