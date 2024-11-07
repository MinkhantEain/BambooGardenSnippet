package com.example.bamboogarden.menu.menuBill

import android.graphics.Bitmap
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.R
import com.example.bamboogarden.common.ComposableImage.ScreenshotComposableWrapper
import com.example.bamboogarden.common.DashedDivider
import com.example.bamboogarden.common.LoadingOverlay
import com.example.bamboogarden.common.ReceiptFormat.ReceiptFormat
import com.example.bamboogarden.common.dialogs.errorDialog.ErrorDialog
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.menu.menuCommon.colorOnStatus
import com.example.bamboogarden.ui.theme.avenirFamily
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun MenuBillScreen(
  tableId: String,
  onBackClick: () -> Unit,
  onAddMoreDish: () -> Unit,
  showBluetoothConnectionScreen: () -> Unit,
) {
  val TAG = "MenuBillScreen"
  val viewModel: MenuBillScreenViewModel = viewModel(initializer = {
    MenuBillScreenViewModel(
      tableId = tableId,
      savedStateHandle = this.createSavedStateHandle(),
      repo = BambooGardenApplication.appModule.menuRepository,
    )
  })

  val backClick = {
    viewModel.isLoading.value = true
    viewModel.viewModelScope.launch {
      if (viewModel.allPaid.value) {
        viewModel.clearBill() {
          viewModel.updatePresence()
        }
      } else {
        viewModel.updatePresence()
      }
    }.invokeOnCompletion {
      viewModel.isLoading.value = false
      onBackClick()
    }
    Unit
  }

  BackHandler {
    backClick()
  }

  val text by viewModel.totalCost.collectAsState(initial = 0)



  AndroidView(
    factory = { ctx ->
      val view = ScreenshotComposableWrapper(
        context = ctx,
        onBitmapCreated = { bitmap: Bitmap ->
          viewModel.bitmapCreated(bitmap)
        },
        component = {
          ReceiptFormat(
            tableId = tableId, vm = viewModel,
          )
        },
      )
      view
    },
  )

  Box {
    ErrorDialog(controller = viewModel.errorDialogController)

    if (viewModel.isLoading.value) {
      LoadingOverlay()
    }

    MenuBillDetails(
      tableId = tableId,
      viewModel = viewModel,
      onBackClick = backClick,
      modifier = Modifier.verticalScroll(rememberScrollState()),
      onAddMoreDish = onAddMoreDish,
      showBluetoothConnectionScreen = showBluetoothConnectionScreen
    )
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBillDetails(
  modifier: Modifier = Modifier, tableId: String, viewModel: MenuBillScreenViewModel,
  onBackClick: () -> Unit,
  onAddMoreDish: () -> Unit,
  showBluetoothConnectionScreen: () -> Unit,
) {
  val wrapper by remember { viewModel.wrapper }

  Scaffold(modifier = Modifier, topBar = {
    TopAppBar(title = {
      Text(
        text = tableId, fontSize = TextUnit(30f, TextUnitType.Sp), fontWeight = FontWeight.Bold
      )
    }, colors = TopAppBarColors(
      containerColor = if (viewModel.allPaid.value) Color(
        156, 255, 113, 255
      ) else TopAppBarDefaults.topAppBarColors().containerColor,
      actionIconContentColor = TopAppBarDefaults.topAppBarColors().actionIconContentColor,
      scrolledContainerColor = TopAppBarDefaults.topAppBarColors().scrolledContainerColor,
      titleContentColor = TopAppBarDefaults.topAppBarColors().titleContentColor,
      navigationIconContentColor = TopAppBarDefaults.topAppBarColors().titleContentColor,
    ), navigationIcon = {
      IconButton(onClick = onBackClick) {
        Icon(
          painter = painterResource(id = R.drawable.back_arrow),
          tint = Color(1, 127, 161, 255),
          contentDescription = "Back to MenuTableScreen"
        )
      }
    }, actions = {
      ElevatedButton(onClick = {
        try {
          viewModel.onBitmapGenerated.value?.run {
            viewModel.printBill(this)
          }
        } catch (e: Exception) {
          Log.d("Printing", "MenuBillDetails: $e")
        }
        if (viewModel.showBluetoothDeviceSelectionPage.value) {
          showBluetoothConnectionScreen()
          viewModel.showBluetoothDeviceSelectionPage.value = false
        }
      }, colors = ButtonColors(Color.White, Color.White, Color.White, Color.White)) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
          contentDescription = "Print Bill",
          tint = Color(12, 192, 223, 255)
        )
      }
      if (!viewModel.allPaid.value)
        ElevatedButton(
          onClick = onAddMoreDish,
          colors = ButtonColors(Color.White, Color.White, Color.White, Color.White)
        ) {
          Text(
            text = "Add+", fontSize = 18.sp, color = Color(12, 192, 223, 255)
          )
        }
    })
  }) {
    Column(
      modifier = modifier.padding(it)
    ) {
      val fontSize = 17.sp
      Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row {
            Text(
              text = tableId,
              fontSize = fontSize,
              modifier = Modifier.padding(end = 10.dp),
              fontFamily = avenirFamily
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
              text = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm")),
              fontWeight = FontWeight.SemiBold,
              fontFamily = avenirFamily,
              fontSize = 16.sp
            )
          }
          Text(
            text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")),
            fontWeight = FontWeight.SemiBold,
            fontFamily = avenirFamily,
            fontSize = fontSize,
          )
        }
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp, horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "Qty",
              modifier = Modifier.fillMaxWidth(.1f),
              fontSize = fontSize,
              textAlign = TextAlign.Start,
              fontFamily = avenirFamily
            )
            Text(
              text = "Description",
              modifier = Modifier.fillMaxWidth(.6f),
              fontSize = fontSize,
              textAlign = TextAlign.Center,
              fontFamily = avenirFamily
            )
            Text(
              text = "Price",
              modifier = Modifier.fillMaxWidth(.45f),
              textAlign = TextAlign.End,
              fontSize = fontSize,
              fontFamily = avenirFamily
            )
            Text(
              text = "Total",
              modifier = Modifier.fillMaxWidth(),
              fontSize = fontSize,
              textAlign = TextAlign.End,
              fontFamily = avenirFamily
            )
          }
        }
        DashedDivider(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp, top = 0.dp),
          color = Color.Black
        )
      }
      for (index in wrapper.indices) {
        val currentWrapper = wrapper[index]
        val dish = currentWrapper.dishList.first().dish
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Text(
              text = currentWrapper.count.toString(),
              modifier = Modifier.fillMaxWidth(.1f),
              fontSize = 15.sp,
              textAlign = TextAlign.Start,
            )
            Text(
              text = dish.name,
              modifier = Modifier.fillMaxWidth(.6f),
              overflow = TextOverflow.Ellipsis,
              maxLines = 3,
              fontSize = 15.sp,
              color = colorOnStatus(currentWrapper.dishList.first().status),
            )
            Text(
              text = dish.price.toCurrency(),
              modifier = Modifier.fillMaxWidth(.45f),
              textAlign = TextAlign.End,
              fontSize = 15.sp,
            )
            Text(
              text = (dish.price * currentWrapper.count).toCurrency(),
              modifier = Modifier.fillMaxWidth(),
              fontSize = 15.sp,
              textAlign = TextAlign.End,
            )
          }
        }
      }
      Column(
        modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
      ) {
        HorizontalDivider(
          modifier = Modifier.padding(top= 5.dp), thickness = 1.dp, color = Color.Black
        )
        FooterComponent(
          text = "Cost", amount = viewModel.cost.collectAsState().value,
          fontSize = 14.sp, fontWeight = FontWeight.Normal
        )
        FooterComponent(
          text = "Service Charge 5%",
          amount = viewModel.serviceCharge.collectAsState(initial = 0).value,
          fontSize = 14.sp,
          fontWeight = FontWeight.Normal,
          isClickable = !viewModel.allPaid.value,
          onClick = { viewModel.toggle(MenuBillScreenViewModel.HAS_SERVICE_CHARGE) },
          include = viewModel.hasServiceCharge.collectAsState().value
        )
        FooterComponent(
          text = "Tax 5%", amount = viewModel.tax.collectAsState(0).value,
          fontSize = 14.sp, fontWeight = FontWeight.Normal,
          isClickable = !viewModel.allPaid.value,
          onClick = { viewModel.toggle(MenuBillScreenViewModel.HAS_TAX) },
          include = viewModel.hasTax.collectAsState().value
        )
        HorizontalDivider(thickness = 1.dp, color = Color.Black, modifier = Modifier.padding(bottom = 10.dp))
        FooterComponent(
          text = "Total Cost",
          amount = viewModel.totalCost.collectAsState(initial = viewModel.cost.collectAsState().value).value
        )
        HorizontalDivider(thickness = 1.dp, color = Color.Black)
        HorizontalDivider(
          modifier = Modifier.padding(vertical = 3.dp), thickness = 1.dp, color = Color.Black
        )
        Text(
          text = "မင်္ဂလာရှိသောနေ့လေးပါခဗျာ\uD83D\uDE4F",
          fontSize = TextUnit(18f, TextUnitType.Sp),
          modifier = Modifier.padding(vertical = 5.dp)
        )
        if (viewModel.paymentCollectable.value) Button(
          onClick = { viewModel.collectPayment() }, colors = ButtonColors(
            Color.White,
            Color.White,
            Color.White,
            Color.White,
          ), modifier = Modifier.padding(bottom = 30.dp)
        ) {
          Text(
            text = "collect",
            fontSize = TextUnit(30f, TextUnitType.Sp),
            color = Color(98, 197, 97, 255)
          )
        }
      }
    }
  }
}

@Composable
fun FooterComponent(
  onClick: () -> Unit = {},
  isClickable: Boolean = false,
  text: String, amount: Int,
  fontSize: TextUnit = 20.sp,
  fontWeight: FontWeight = FontWeight.Bold,
  include: Boolean = true,
) {
  Row(
    modifier = Modifier
      .padding(horizontal = 20.dp)
      .fillMaxWidth()
      .clickable(enabled = isClickable) { onClick() },
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = text, fontSize = fontSize, fontWeight = fontWeight,
      color = if (!include) Color.White else Color.Black,
    )
    Text(
      text = amount.toCurrency(), fontSize = fontSize, fontWeight = fontWeight,
      color = if (!include) Color.White else Color.Black,
    )
  }
}