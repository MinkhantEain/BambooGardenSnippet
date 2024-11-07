package com.example.bamboogarden.chef.chefHistory

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.R
import com.example.bamboogarden.common.TextBoxDateSelector.CustomDatePicker
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChefHistoryScreen(
  onBackButtonClick: () -> Unit
) {
  val viewModel: ChefHistoryScreenViewModel = viewModel {
    ChefHistoryScreenViewModel(BambooGardenApplication.appModule.chefRemoteRepository)
  }
  val showDatePicker by remember { viewModel.showDialog }
  val deleted by remember { viewModel.deleted }
  val date by remember { viewModel.date }

  Scaffold(
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Delete History",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp),
          )
        },
        navigationIcon = {
          IconButton(onClick = onBackButtonClick) {
            Icon(
              contentDescription = null,
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
        val historyValueFontSize = 25.sp
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
            Icon(imageVector = Icons.Filled.AccessTime, contentDescription = null,
              modifier = Modifier.width(70.dp)
                )
//            Text(
//              "Time",
//              fontSize = historyValueFontSize,
//              fontWeight = FontWeight.ExtraBold,
//              color = historyValueColor
//            )
            Text(
              "Table",
              fontSize = historyValueFontSize,
              fontWeight = FontWeight.ExtraBold,
              color = historyValueColor,
              modifier = Modifier.width(70.dp)
            )
            Text(
              "Dish",
              fontSize = historyValueFontSize,
              fontWeight = FontWeight.ExtraBold,
              modifier = Modifier.width(200.dp),
              textAlign = TextAlign.Center,
              color = historyValueColor
            )
          }
        }
        items(count = deleted.size) { index ->
          val delete = deleted[index]
          Row(
            modifier =
            Modifier
              .fillMaxSize()
              .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              delete.time.take(5),
              fontSize = historyValueFontSize,
              color = historyValueColor,
              modifier = Modifier.width(70.dp)
            )
            Text(
              delete.tableId,
              fontSize = historyValueFontSize,
              fontWeight = FontWeight.ExtraBold,
              color = historyValueColor
            )
            Text(
              delete.dish.name,
              fontSize = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              color = historyValueColor,
              modifier = Modifier
                .width(200.dp)
            )
          }
        }
      }
    }
  }
}