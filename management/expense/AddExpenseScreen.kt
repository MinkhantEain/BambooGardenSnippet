package com.example.bamboogarden.management.expense

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.R
import com.example.bamboogarden.common.ConfirmationDialog
import com.example.bamboogarden.common.CustomDropdownMenu
import com.example.bamboogarden.common.camera.rotateBitmap
import com.example.bamboogarden.common.isNumber
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.common.toNumber
import com.example.bamboogarden.management.expense.data.ExpenseAbout

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
  onBackButtonClick: () -> Unit = {},
  onCameraButtonClick: () -> Unit = {}
) {

  val viewModel: AddExpenseScreenViewModel = viewModel()
  val cameraCheck = remember { viewModel.hasImage }
  val quantityCheck = remember { viewModel.hasQuantity }
  val comment = remember {
    viewModel.comment
  }
  val quantity = remember {
    viewModel.quantity
  }
  val amount = remember {
    viewModel.amount
  }

  Box {
    if (viewModel.isLoading.value)
      AlertDialog(onDismissRequest = { /*TODO*/ }, confirmButton = { /*TODO*/ },
        title = { Text("Loading") }, text = { Text(text = "Loading...") })

    if (viewModel.error.value != null)
      AlertDialog(
        onDismissRequest = { viewModel.error.value = null },
        confirmButton = {},
        text = { viewModel.error.value?.let { Text(text = it) } },
        title = { Text(text = "Error!!!") }
      )
    if (viewModel.showConfirmationDialog.value)
      ConfirmationDialog(
        onDismissRequest = { viewModel.showConfirmationDialog.value = false },
        confirmButton = {
          ElevatedButton(onClick = viewModel::submitExpenseReport) {
            Text(text = "OK")
          }
        },
        contentMap = mutableMapOf(
          "အကြောင်းရာ" to viewModel.about.value,
          "comment" to viewModel.comment.value,
        ).apply {
          if (viewModel.hasQuantity.value)
            this["အရေတွက်/အလေးချိန်"] = viewModel.quantity.value
          this["ကျသင့်ငွေ(ကျပ်)"] = if (viewModel.amount.value == "") "0" else viewModel.amount.value.toNumber().toCurrency()
        }
      )
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text("ထွက်ငွေ") },
          navigationIcon = {
            IconButton(onClick = onBackButtonClick) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Button"
              )
            }
          }
        )
      }
    ) {
      Column(
        modifier =
        Modifier
          .fillMaxWidth()
          .padding(it)
          .imePadding()
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row {
          IconButton(
            onClick = onCameraButtonClick,
            modifier = Modifier.size(250.dp),
            enabled = cameraCheck.value
          ) {
            if (cameraCheck.value)
              if (viewModel.image != null) {
                viewModel.image?.rotateBitmap(90)?.asImageBitmap()?.let { it1 ->
                  Image(
                    bitmap = it1,
                    contentDescription = null,
                    modifier = Modifier.size(210.dp)
                  )
                }
              } else {
                Image(
                  painter = painterResource(id = R.drawable.camera),
                  contentDescription = "Camera",
                  modifier = Modifier.size(210.dp)
                )
              }
            else
              Image(
                painter = painterResource(id = R.drawable.camera_crossed),
                contentDescription = "Camera",
                modifier = Modifier.size(210.dp)
              )
          }
          Checkbox(
            checked = cameraCheck.value,
            onCheckedChange = { cameraCheck.value = !cameraCheck.value },
          )
        }
        Text(
          text = "ဘောင်ချာပုံ",
          color = Color(71, 89, 132, 255),
          fontWeight = FontWeight.SemiBold
        )

        HorizontalDivider()

        CustomDropdownMenu(about = viewModel.about, fullArray = ExpenseAbout)

        OutlinedTextField(
          value = comment.value,
          onValueChange = { s -> comment.value = s },
          label = { Text(text = "အကြောင်းရာ") })

        OutlinedTextField(
          value = quantity.value,
          onValueChange = { s -> if (s.isNumber() || s.isEmpty()) quantity.value = s },
          label = { Text(text = "အရေတွက်/အလေးချိန်") },
          trailingIcon = {
            Checkbox(
              checked = quantityCheck.value,
              onCheckedChange = {
                quantityCheck.value = !quantityCheck.value
                quantity.value = ""
              }
            )
          },
          enabled = quantityCheck.value,
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        OutlinedTextField(
          value = amount.value,
          onValueChange = { s -> if (s.isNumber() || s.isEmpty()) amount.value = s },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          label = { Text(text = "ကျသင့်ငွေ(ကျပ်)") }
        )
        ElevatedButton(onClick = {
          if (viewModel.checkSubmitability()) {
            viewModel.showConfirmationDialog.value = true
          }
        }) { Text(text = "Add") }
      }
    }
  }
}
