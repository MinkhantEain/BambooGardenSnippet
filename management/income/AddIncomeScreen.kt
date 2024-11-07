package com.example.bamboogarden.management.income

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.ConfirmationDialog
import com.example.bamboogarden.common.CustomDropdownMenu
import com.example.bamboogarden.common.dialogs.loading.ProgressiveLoadingDialog
import com.example.bamboogarden.common.isNumber
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.common.toNumber
import com.example.bamboogarden.common.viewModelFactory
import com.example.bamboogarden.management.income.data.IncomeAbout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview(showBackground = true)
fun AddIncomeScreen(
  onBackButtonClick: () -> Unit = {},
) {
  val TAG = "AddIncomeScreen"
  val viewModel: AddIncomeScreenViewModel =
    viewModel(
      factory =
      viewModelFactory {
        AddIncomeScreenViewModel(
          repo = BambooGardenApplication.appModule.incomeRepository
        )
      }
    )
  Box(contentAlignment = Alignment.Center) {
    if (viewModel.error.value != null)
      AlertDialog(
        onDismissRequest = { viewModel.error.value = null },
        confirmButton = {},
        text = { viewModel.error.value?.let { Text(text = it) } },
        title = { Text(text = "Error!!!") }
      )
    ProgressiveLoadingDialog(controller = viewModel.loadingController)
    if (viewModel.showConfirmationDialog.value) {
      ConfirmationDialog(onDismissRequest = {
        viewModel.showConfirmationDialog.value = false
      }, confirmButton = {
        ElevatedButton(onClick = viewModel::submitIncomeReport) {
          Text(text = "OK")
        }
      },
        contentMap = mapOf(
          "About" to viewModel.about.value,
          "အကြောင်းရာ" to viewModel.comment.value,
          "ရောင်းရငွေ(ကျပ်)" to viewModel.amount.value.text.toNumber().toCurrency()
        )
      )
    }
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = "ဝင်ငွေ") },
          navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button"
              )
            }
          }
        )
      }
    ) {
      Column(
        modifier = Modifier.padding(it),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        CustomDropdownMenu(about =  viewModel.about, fullArray =  IncomeAbout, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(
          value = viewModel.comment.value,
          onValueChange = { s -> viewModel.comment.value = s },
          modifier = Modifier.fillMaxWidth(),
          label = { Text(text = "အကြောင်းရာ") }
        )
        OutlinedTextField(
          value = viewModel.amount.value,
          onValueChange = { s ->
            if (s.text.isNumber() || s.text.isEmpty()) viewModel.amount.value = s
          },
          modifier = Modifier.fillMaxWidth(),
          label = { Text(text = "ရောင်းရငွေ(ကျပ်)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        ElevatedButton(
          onClick = {
            if (viewModel.checkSubmitability())
              viewModel.showConfirmationDialog.value = true
          },
          modifier = Modifier.fillMaxWidth(.5f)
        ) {
          Text(
            text = "Add",
            fontSize = TextUnit(20f, TextUnitType.Sp),
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}

