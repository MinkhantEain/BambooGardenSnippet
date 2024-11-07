package com.example.bamboogarden.management.produceExcel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.R
import com.example.bamboogarden.common.TextBoxDateSelector.TextBoxDateSelector
import com.example.bamboogarden.common.dialogs.completionDialog.CompletionDialog
import com.example.bamboogarden.common.dialogs.confirmationDialog.ConfirmationDialog
import com.example.bamboogarden.common.dialogs.loading.ProgressiveLoadingDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProduceExcelScreen(onBackClick: () -> Unit) {
  val viewModel : ProduceExcelScreenViewModel = viewModel()

  CompletionDialog(title ="Excel Production Completed" , text = "The generated Excel file can be found in Download/BambooGarden", controller = viewModel.completionDialogController)

  Scaffold(topBar = {
    TopAppBar(title = { Text(text = "Produce Excel") },
      navigationIcon = {
        IconButton(onClick = onBackClick) {
          Icon(painter = painterResource(id = R.drawable.back_arrow), contentDescription = null)
        }
      },
      actions = {
        TextButton(onClick = viewModel::produceMenuDishExcel) {
          Text(text = "MenuDish Excel")
        }
      }
      )
  }) { paddings ->
    Column(
      modifier = Modifier.padding(paddings),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      ProgressiveLoadingDialog(controller = viewModel.loadingController)
      TextBoxDateSelector(
        modifier = Modifier.fillMaxWidth(),
        controller = viewModel.textBoxDateSelectorController
      )

      ConfirmationDialog(title = "Produce Excel",
        text = "${viewModel.textBoxDateSelectorController.textFieldValue.text} a correct date you want?",
        controller = viewModel.dialogController,
        onConfirm = {
          viewModel.produceExcel()
        })

      ElevatedButton(onClick = { viewModel.dialogController.show() }) {
        Text(text = "Produce")
      }
    }
  }
}