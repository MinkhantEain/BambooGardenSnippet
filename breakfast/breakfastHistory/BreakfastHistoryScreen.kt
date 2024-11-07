package com.example.bamboogarden.breakfast.breakfastHistory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.R
import com.example.bamboogarden.common.TextBoxDateSelector.CustomDatePicker
import com.example.bamboogarden.common.toCurrency
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun BreakfastHistoryScreen(
    onBackButtonClick: () -> Unit = {},
) {
    val viewModel: BreakfastHistoryScreenViewModel = viewModel {
        BreakfastHistoryScreenViewModel(BambooGardenApplication.appModule.breakfastRepository)
    }
    val showDatePicker by remember { viewModel.showDialog }
    val payments by remember { viewModel.payments }
    val date by remember { viewModel.date }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Breakfast History",
                        fontSize = TextUnit(30f, TextUnitType.Sp),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 20.dp),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackButtonClick) {
                        Icon(
                            contentDescription = "Back to Breakfast Table",
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp),
                            painter = painterResource(id = R.drawable.back_arrow),
                            tint = Color(1,127,161,255)
                        )
                    }
                }
            )
        }
    ) {
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
            LazyColumn(contentPadding = it) {
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
                item(key = "Header") {
                    Row(
                        modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Icon(imageVector = Icons.Filled.Person, contentDescription ="Person Icon")
                        Text(
                            "Time",
                            fontSize = historyValueFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = historyValueColor
                        )
                        Text(
                            "Table",
                            fontSize = historyValueFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = historyValueColor
                        )
                        Text(
                            "Cost",
                            fontSize = historyValueFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.width(100.dp),
                            textAlign = TextAlign.End,
                            color = historyValueColor
                        )
                    }
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
                        Text(
                            payment.people.toString(),
                            fontSize = historyValueFontSize,
                            color = historyValueColor
                        )
                        Text(
                            payment.time.take(5),
                            fontSize = historyValueFontSize,
                            color = historyValueColor
                        )
                        Text(
                            payment.tableId,
                            fontSize = historyValueFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            color = historyValueColor
                        )
                        Text(
                            payment.totalCost.toCurrency(),
                            fontSize = historyValueFontSize,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.width(100.dp),
                            textAlign = TextAlign.End,
                            color = historyValueColor
                        )
                    }
                }
            }
        }
    }
}
