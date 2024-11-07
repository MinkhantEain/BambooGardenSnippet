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
import com.example.bamboogarden.common.dialogs.DialogController

@Composable
fun LoadingDialog(controller: DialogController) {
  if (controller.isVisible.value)
    AlertDialog(onDismissRequest = {  }, confirmButton = {  },
      title = { Text(text = "Loading...")},
      text = {
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
          CircularProgressIndicator()
        }
      })
}