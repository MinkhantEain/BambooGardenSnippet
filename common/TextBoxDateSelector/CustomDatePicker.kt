package com.example.bamboogarden.common.TextBoxDateSelector

import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePicker(
    onDismiss: () -> Unit = {},
    onConfirm: (LocalDate) -> Unit = {},
    givenDate: LocalDate,
) {
    val datePickerState =
        rememberDatePickerState(
            initialSelectedDateMillis =
                LocalDateTime.of(givenDate, LocalTime.MIN).toInstant(ZoneOffset.UTC).toEpochMilli()
        )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val instant = Instant.ofEpochMilli(it)
                        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                        onConfirm(date.toLocalDate())
                        onDismiss()
                    }
                }
            ) {
                Text("Confirm")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
