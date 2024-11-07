package com.example.bamboogarden.management.checkTotal

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.common.TextBoxDateSelector.CustomDatePicker
import com.example.bamboogarden.common.toCurrency
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckTotalScreen(modifier: Modifier = Modifier,
                     onBackClick: () -> Unit) {
  val TAG= "CheckTotalScreen"
  val viewModel: CheckTotalScreenViewModel = viewModel()
  Scaffold(
    topBar = {
      TopAppBar(title = { Text(text = "Check Total") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        })
    }
  ) { paddings ->
    if (viewModel.showDatePicker.value)
      CustomDatePicker(givenDate = LocalDate.now(),
        onConfirm = viewModel::changeDate,
        onDismiss = {viewModel.showDatePicker.value = false})
    LazyColumn(contentPadding = paddings, horizontalAlignment = Alignment.CenterHorizontally) {
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          TextField(
            value = viewModel.date.value.toString(), onValueChange = {},
            label = { Text(text = "Date", fontSize = 17.sp) },
            maxLines = 1,
            modifier = Modifier
              .fillMaxWidth()
              .clickable { viewModel.showDatePicker.value = true },
            textStyle = TextStyle(fontSize = 20.sp),
            enabled = false
          )

          Row(modifier= Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            Row(modifier = Modifier.width(150.dp), verticalAlignment = Alignment.CenterVertically) {
              RadioButton(selected = viewModel.checkOption.value == CheckTotalType.Daily, onClick = { viewModel.checkOption.value = CheckTotalType.Daily })
              Text(text = "Daily", modifier = Modifier.width(100.dp),
                fontSize = 18.sp)
            }
            Row(modifier = Modifier.width(150.dp), verticalAlignment = Alignment.CenterVertically) {
              RadioButton(selected = viewModel.checkOption.value == CheckTotalType.Monthly, onClick = { viewModel.checkOption.value = CheckTotalType.Monthly })
              Text(text = "Monthly", modifier = Modifier.width(100.dp),
                fontSize = 18.sp)
            }
          }
        }

        Spacer(modifier = Modifier.height(50.dp))

        HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(vertical = 20.dp))
        val fontSize = 20.sp
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "Breakfast Total:", fontSize = fontSize)
          Text(viewModel.breakfastTotal.intValue.toCurrency(), fontSize = fontSize)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "Customers:", fontSize = fontSize)
          Text(viewModel.breakfastCustomer.intValue.toCurrency(), fontSize = fontSize)
        }

        HorizontalDivider(thickness = 2.dp, color = Color.Black, modifier = Modifier.padding(vertical = 20.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "Lunch Total:", fontSize=fontSize)
          Text(viewModel.menuTotal.intValue.toCurrency(), fontSize = fontSize)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
          Text(text = "Customers:", fontSize = fontSize)
          Text(viewModel.menuCustomer.intValue.toCurrency(), fontSize = fontSize)
        }

        Spacer(modifier = Modifier.height(30.dp))
      }
      
      items(viewModel.menuDishAndCount.value.size) {index ->  
        Row(modifier= Modifier.fillMaxSize(), horizontalArrangement = Arrangement.SpaceBetween) {
          val key = viewModel.menuDishAndCount.value.keys.elementAt(index)
          Text(text = key)
          Text(text = viewModel.menuDishAndCount.value[key].toString())
        }
      }

    }
  }
}