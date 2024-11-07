package com.example.bamboogarden.common.dialogs.confirmationDialog

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.bamboogarden.common.dialogs.DialogController

@Composable
fun ConfirmationDialog(
  title: String, text: String, controller: DialogController, onCancel: () -> Unit = {},
  onConfirm: () -> Unit
) {
  if (controller.isVisible.value) {
    AlertDialog(onDismissRequest = {
      controller.hide()
    }, confirmButton = {
      Row {
        ElevatedButton(onClick = {
          onCancel()
          controller.hide()
        }) {
          Text(text = "Cancel")
        }
        ElevatedButton(onClick = {
          onConfirm()
          controller.hide()
        }) {
          Text(text = "Confirm")
        }
      }
    }, title = { Text(text = title) },
      text = { Text(text = text) })
  }
}