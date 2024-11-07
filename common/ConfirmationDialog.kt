package com.example.bamboogarden.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ConfirmationDialog(modifier: Modifier = Modifier,
                       onDismissRequest: () -> Unit,
                       confirmButton:@Composable () -> Unit,
                       contentMap: Map<String, String>) {
  AlertDialog(onDismissRequest = onDismissRequest, confirmButton = confirmButton,
    title = { Text(text = "Confirm submission?") },
    text = {
      Column {
        contentMap.forEach { (title, about) ->
          Row {
            Text(text = "$title: ", fontWeight = FontWeight.Bold)
            Text(text = about)
          }
        }
      }
    })
}