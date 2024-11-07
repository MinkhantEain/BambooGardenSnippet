package com.example.bamboogarden.management

import MainScreenButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun ManagementScreen(
    onAddExpenseButtonClick: () -> Unit = {},
    onAddIncomeButtonClick: () -> Unit = {},
    onDailyReportButtonClick: () -> Unit = {},
    onCheckTotalButtonClick: () -> Unit = {},
    onProduceExcelButtonClick: () -> Unit = {},
    onBackButtonClick: () -> Unit = {},
) {
    Scaffold(
        topBar =
        {
            TopAppBar(
                title = { Text("Management") },
                navigationIcon = {
                    IconButton(onClick = onBackButtonClick) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back Arrow")
                    }
                }
            )
        }
    ) {
        Column(modifier = Modifier
            .padding(it)
            .padding(horizontal = 40.dp)
            .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            ) {
            MainScreenButton(text = "Add ဝင်ငွေ") {
                onAddIncomeButtonClick()
            }
            MainScreenButton(text = "Add ထွက်ငွေ") {
                onAddExpenseButtonClick()
            }
            MainScreenButton(text = "Daily Report") {
                onDailyReportButtonClick()
            }
            MainScreenButton(text = "Check Total") {
                onCheckTotalButtonClick()
            }
            MainScreenButton(text = "Produce Excel") {
                onProduceExcelButtonClick()
            }
        }
    }
}
