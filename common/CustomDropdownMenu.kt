package com.example.bamboogarden.common

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import kotlin.enums.EnumEntries

fun <T : Enum<T>> EnumEntries<T>.names() = this.map { it.name }

@Composable
fun  CustomDropdownMenu(modifier: Modifier = Modifier, about: MutableState<String>, fullArray: Array<String>) {

  val TAG = "CustomDropdownMenu"
  val isExpand = remember { mutableStateOf(false) }

  Column {
    OutlinedTextField(
      modifier = modifier,
      value = about.value,
      onValueChange = {},
      label = { Text(text = "About") },
      readOnly = true,
      trailingIcon = {
        IconButton(
          onClick = {
            isExpand.value = true
            Log.d(TAG, "CustomDropdownMenu: Clicked")
          }
        ) {
          Icon(
            imageVector = Icons.Filled.ArrowDropDown,
            contentDescription = "Dropdown Icon"
          )
        }
      },
    )

    DropdownMenu(
      expanded = isExpand.value,
      onDismissRequest = {
        isExpand.value = false
        Log.d(TAG, "CustomDropdownMenu: Dismissed")
      },
      modifier = modifier
    ) {
      fullArray.forEach { value ->
        DropdownMenuItem(
          text = { Text(text = value) },
          onClick = {
            about.value = value
            isExpand.value = false
          }
        )
      }
    }
  }
}


