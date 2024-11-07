package com.example.bamboogarden.common.dialogs.errorDialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ErrorDialog(
  controller: ErrorDialogController
) {
  if (controller.isVisible.value) {
    AlertDialog(onDismissRequest = {
      controller.hide()
    }, confirmButton = {
    }, title = { Text(text = controller.text.value) },
      text = { Text(text = controller.title.value) })
  }
}
