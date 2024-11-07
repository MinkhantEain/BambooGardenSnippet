package com.example.bamboogarden.breakfast.breakfastCollected

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.R
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.common.viewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakfastCollectedScreen(
    tableId: String,
    onBackClick: () -> Unit,
    collectedAmount: Int,
) {
    val viewModel: BreakfastCollectedScreenViewModel =
        viewModel(factory = viewModelFactory { BreakfastCollectedScreenViewModel(onBackClick) })

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.back_arrow),
                            tint = Color(1,127,161,255),
                            contentDescription = "Back Icon"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = tableId,
                fontSize = TextUnit(40f, TextUnitType.Sp),
                color = Color(19, 171, 37, 255),
                modifier = Modifier.padding(bottom = 20.dp),
                fontWeight = FontWeight.Bold
            )
            Text(
                text = collectedAmount.toCurrency(),
                fontSize = TextUnit(40f, TextUnitType.Sp),
                color = Color(19, 171, 37, 255),
                modifier = Modifier.padding(bottom = 20.dp),
                fontWeight = FontWeight.Bold
            )

            Icon(
                painter = painterResource(id = R.drawable.check_within_circle),
                contentDescription = "Check Icon",
                tint = Color(19, 171, 37, 255),
                modifier = Modifier.size(80.dp)
            )
        }
    }
}
