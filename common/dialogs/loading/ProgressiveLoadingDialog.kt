package com.example.bamboogarden.common.dialogs.loading

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun ProgressiveLoadingDialog(controller: LoadingController) {
  if (controller.isVisible.value)
    AlertDialog(onDismissRequest = {  }, confirmButton = {  },
    title = { Text(text = "Loading...")},
    text = {
      Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(progress = { controller.progress.floatValue })
        Text(text = controller.progress.floatValue.toPercentage())
      }
    })
}

fun Float.toPercentage() : String {
  return (this * 100f).toInt().toString() + "%"
}