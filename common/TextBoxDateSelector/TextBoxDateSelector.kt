package com.example.bamboogarden.common.TextBoxDateSelector

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TextBoxDateSelector(modifier: Modifier = Modifier, controller: TextBoxDateSelectorController) {
  val TAG = "TextBoxDateSelector"


  TextField(
    value = controller.textFieldValue,
    onValueChange = { },
    modifier = modifier.clickable {
      Log.d(TAG, "TextBoxDateSelector: clickable executed")
      controller.showDatePicker()
    },
    maxLines = 1,
    leadingIcon = {
      Icon(
        imageVector = Icons.Filled.DateRange, contentDescription = "Date Icon"
      )
    },
    enabled = false,
  )

  if (controller.isVisible.value) {
    CustomDatePicker(
      givenDate = controller.date,
      onConfirm = controller::selectDate,
      onDismiss = controller::hideDatePicker,
    )
  }
}