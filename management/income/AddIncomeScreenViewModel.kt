package com.example.bamboogarden.management.income

import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.dialogs.loading.LoadingController
import com.example.bamboogarden.management.income.data.Income
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class AddIncomeScreenViewModel(private val repo: IncomeRepository) : ViewModel() {
    val about = mutableStateOf("Breakfast")
    val amount = mutableStateOf(TextFieldValue(""))
    val comment = mutableStateOf("")
    val error = mutableStateOf<String?>(null)
    val completed = mutableStateOf(false)
    val showConfirmationDialog = mutableStateOf(false)
    val loadingController = LoadingController()

    fun checkSubmitability() : Boolean {
        if (comment.value.isEmpty()) {
            error.value = "အကြောင်းရာ cannot be empty"
            return false
        }
        if (amount.value.text.isEmpty()) {
            error.value = "ရောင်းရငွေ(ကျပ်) cannot be empty"
            return false
        }
        return true
    }

    private fun successfulSubmission() {
        Toast.makeText(BambooGardenApplication.instance, "Add Successful", Toast.LENGTH_SHORT).show()
        amount.value = TextFieldValue("")
        comment.value = ""
        about.value = "Breakfast"
    }

    fun submitIncomeReport() {
        loadingController.show()
        viewModelScope.launch {
            try {
                repo.addIncomeRecord(
                    Income(
                        about = about.value,
                        amount = if (amount.value.text.isEmpty()) 0 else amount.value.text.toInt(),
                        comment = comment.value,
                        time = LocalTime.now().toString(),
                        date = LocalDate.now().toString()
                    )
                )
                completed.value = true
                loadingController.hide()
                showConfirmationDialog.value = false
                successfulSubmission()
            } catch (e: Exception) {
                error.value = e.message
                loadingController.hide()
                showConfirmationDialog.value = false
            }
        }
    }
}
