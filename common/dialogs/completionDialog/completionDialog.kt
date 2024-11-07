package com.example.bamboogarden.common.dialogs.completionDialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.bamboogarden.common.dialogs.DialogController

@Composable
fun CompletionDialog(
  title: String, text: String, controller: DialogController,
  onDismissCallback: () -> Unit = {},
) {
  if (controller.isVisible.value) {
    AlertDialog(onDismissRequest = {
      onDismissCallback()
      controller.hide()
    }, confirmButton = {
    }, title = { Text(text = title) },
      text = { Text(text = text) })
  }
}