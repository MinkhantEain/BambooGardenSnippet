package com.example.bamboogarden.management.dailyRecord

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.bamboogarden.R
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.management.expense.data.Expense
import com.example.bamboogarden.management.income.data.Income

@Composable
fun ExpenseDialog(
  expenseState: MutableState<Expense>,
  isVisible: MutableState<Boolean>,
  bitmap: MutableState<Bitmap?>
) {
  val TAG = "ExpenseDialog"
  val expense by remember {
    expenseState
  }
  if (isVisible.value)
    AlertDialog(onDismissRequest = { isVisible.value = false }, confirmButton = { /*TODO*/ },
      title = { Text(text = "Expense") },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          if (bitmap.value == null) {
            Log.d(TAG, "ExpenseDialog: no image")
            Image(
              painter = painterResource(id = R.drawable.image_not_found),
              contentDescription = null,
              modifier = Modifier.size(300.dp)
            )
          } else {
            Log.d(TAG, "ExpenseDialog: has image")
            Image(
              bitmap = bitmap.value!!.asImageBitmap(),
              contentDescription = null,
              modifier = Modifier.size(300.dp)
            )
          }
          Text(text = expense.time, modifier = Modifier.align(Alignment.CenterHorizontally))

          Row {
            Text(text = "About:")
            Text(text = expense.about)
          }
          Row {
            Text(text = "Quantity:")
            Text(text = expense.quantity.toString())
          }
          Row {
            Text(text = "Amount:")
            Text(text = expense.amount.toCurrency())
          }
          Row {
            Text(text = "Comment:")
            Text(text = expense.comment)
          }
        }
      })
}

@Composable
fun IncomeDialog(incomeState: MutableState<Income>,
                 isVisible: MutableState<Boolean>) {
  val income by remember {
    incomeState
  }
  if (isVisible.value)
    AlertDialog(onDismissRequest = { isVisible.value = false }, confirmButton = { /*TODO*/ },
      title = { Text(text = "Income") },
      text = {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
        ) {
          Text(text = income.time, modifier = Modifier.align(Alignment.CenterHorizontally))
          Row {
            Text(text = "About:")
            Text(text = income.about)
          }
          Row {
            Text(text = "Comment:")
            Text(text = income.comment)
          }
          Row {
            Text(text = "Amount:")
            Text(text = income.amount.toCurrency())
          }
        }
      })
}