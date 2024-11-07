package com.example.bamboogarden.management.dailyRecord

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.common.LoadingOverlay
import com.example.bamboogarden.common.TextBoxDateSelector.CustomDatePicker
import com.example.bamboogarden.common.dialogs.completionDialog.CompletionDialog
import com.example.bamboogarden.common.toCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyRecordScreen(onBackClick: () -> Unit) {
  val viewModel: DailyRecordScreenViewModel = viewModel()
  val date = remember {
    viewModel.selectedDate
  }
  val showDatePicker = remember {
    viewModel.showDatePicker
  }

  ExpenseDialog(expenseState = viewModel.selectedExpense, isVisible =  viewModel.expenseDialogVisibility, bitmap = viewModel.bitmap)
  IncomeDialog(incomeState = viewModel.selectedIncome, isVisible = viewModel.incomeDialogVisibility)
  if (viewModel.isLoading.value) {
    LoadingOverlay()
  }

  CompletionDialog(title = "Error", text = if (viewModel.error.value == null) "" else viewModel.error.value!!.message!! , controller = viewModel.errorDialog, onDismissCallback = {
    viewModel.error.value = null
  })

  Scaffold(
    topBar = {
      TopAppBar(title = { Text(text = "Daily Record") },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
          }
        })
    }
  ) { paddings ->
    LazyColumn(contentPadding = paddings) {
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (showDatePicker.value)
            CustomDatePicker(
              givenDate = date.value,
              onDismiss = { showDatePicker.value = false },
              onConfirm = { selectedDate -> viewModel.changeDate(selectedDate) })
          OutlinedTextField(value = date.value.toString(), onValueChange = {}, enabled = false,
            label = { Text(text = "Date") }, modifier = Modifier
              .fillMaxWidth(.8f)
              .clickable { showDatePicker.value = true })
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Carry On Balance: ")
            Text(text = viewModel.carryOnBalance.intValue.toCurrency())
          }
          Spacer(modifier = Modifier.height(30.dp))

          Text(text = "Income", fontSize = 25.sp, fontWeight = FontWeight.Bold)
          HorizontalDivider(thickness = 1.dp, color = Color.Black)
          Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = "About",
              modifier = Modifier
                .width(90.dp)
              , textAlign = TextAlign.Start)
            Text(text = "Comment",
              modifier = Modifier
                .width(180.dp)
              , textAlign = TextAlign.Start)
            Text(
              text = "$",
              modifier = Modifier
                .width(80.dp),
              textAlign = TextAlign.Center
            )
          }
          HorizontalDivider(thickness = 1.dp, color = Color.Black)
        }
      }
      items(viewModel.incomes.value.size) { index ->
        val income = viewModel.incomes.value[index]
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable { viewModel.selectIncome(income) },
          horizontalArrangement = Arrangement.SpaceBetween
        ) {

          Text(text = income.about, modifier = Modifier
            .width(90.dp), textAlign = TextAlign.Start)
          Text(text = income.comment, modifier = Modifier
            .width(180.dp)
            .heightIn(max = 70.dp)
            .verticalScroll(
              rememberScrollState()
            ))
          Text(text = income.amount.toCurrency(), modifier= Modifier.width(80.dp), textAlign = TextAlign.End)
        }
      }
      item {
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          HorizontalDivider(thickness = 1.dp, color = Color.Black)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Total")
            Text(text = viewModel.incomes.value.fold(0) { acc, income -> acc + income.amount }
              .toCurrency())
          }
          HorizontalDivider(
            thickness = 1.dp,
            color = Color.Black,
            modifier = Modifier.padding(vertical = .5.dp)
          )
          HorizontalDivider(thickness = 1.dp, color = Color.Black)

          Spacer(modifier = Modifier.height(30.dp))

          Text(text = "Expense", fontSize = 25.sp, fontWeight = FontWeight.Bold)
          HorizontalDivider(thickness = 1.dp, color = Color.Black)
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp), horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "About",
              modifier = Modifier
                .width(90.dp),
              textAlign = TextAlign.Center
            )
            Text(
              text = "Comment",
              modifier = Modifier
                .width(180.dp),
              textAlign = TextAlign.Center
            )
            Text(
              text = "$",
              modifier = Modifier
                .width(80.dp),
              textAlign = TextAlign.Center
            )
          }
          HorizontalDivider(thickness = 1.dp, color = Color.Black)
        }
      }
      items(viewModel.expenses.value.size) { index ->
        val expense = viewModel.expenses.value[index]
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable { viewModel.selectExpense(expense) },
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(
            text = expense.about,
            modifier = Modifier
              .width(90.dp),
          )
          Text(
            text = expense.comment,
            modifier = Modifier
              .width(200.dp)
              .heightIn(max = 60.dp)
              .verticalScroll(rememberScrollState()),
            minLines = 1,
          )
          Text(
            text =
            expense.amount.toCurrency(), modifier = Modifier
              .width(80.dp),
            textAlign = TextAlign.End
          )
        }
      }

      item {
        HorizontalDivider(thickness = 1.dp, color = Color.Black)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Total")
          Text(text = viewModel.expenses.value.fold(0) { acc, expense -> acc + expense.amount }
            .toCurrency())
        }
        HorizontalDivider(
          thickness = 1.dp,
          color = Color.Black,
          modifier = Modifier.padding(vertical = .5.dp)
        )
        HorizontalDivider(thickness = 1.dp, color = Color.Black)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "Closing Balance:")
          Text(text = viewModel.closingBalance.intValue.toCurrency())
        }
      }
    }
  }
}