package com.example.bamboogarden.common.ReceiptFormat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bamboogarden.common.DashedDivider
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.menu.menuBill.FooterComponent
import com.example.bamboogarden.menu.menuBill.MenuBillScreenViewModel
import com.example.bamboogarden.ui.theme.avenirFamily
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun ReceiptFormat(
  tableId: String,
  vm: MenuBillScreenViewModel,
) {
  val TAG = "MenuBillScreen"
  val wrapper = vm.wrapper.value
  Surface() {
    val fontSize = 17.sp
    Column {
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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
              text = "Qty", modifier = Modifier.fillMaxWidth(.1f), fontSize = fontSize,
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
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
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
          modifier = Modifier.padding(
            top = 10.dp,
            bottom = if (vm.hasTax.collectAsState().value or vm.hasServiceCharge.collectAsState().value) 0.dp else 10.dp
          ), thickness = 1.dp, color = Color.Black
        )
        if (vm.hasTax.collectAsState().value or vm.hasServiceCharge.collectAsState().value)
          FooterComponent(
            text = "Cost", amount = vm.cost.collectAsState().value,
            fontSize = 14.sp, fontWeight = FontWeight.Normal
          )
        if (vm.hasServiceCharge.collectAsState().value)
          FooterComponent(
            text = "Service Charge (5%)",
            amount = vm.serviceCharge.collectAsState(initial = 0).value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            isClickable = !vm.allPaid.value,
            onClick = { vm.toggle(MenuBillScreenViewModel.HAS_SERVICE_CHARGE) },
            include = vm.hasServiceCharge.collectAsState().value
          )
        if (vm.hasTax.collectAsState().value)
          FooterComponent(
            text = "Tax (5%)", amount = vm.tax.collectAsState(0).value,
            fontSize = 14.sp, fontWeight = FontWeight.Normal,
            isClickable = !vm.allPaid.value,
            onClick = { vm.toggle(MenuBillScreenViewModel.HAS_TAX) },
            include = vm.hasTax.collectAsState().value
          )
        if (vm.hasTax.collectAsState().value or vm.hasServiceCharge.collectAsState().value)
          HorizontalDivider(
            thickness = 1.dp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 10.dp)
          )
        FooterComponent(
          text = "Total Cost",
          amount = vm.totalCost.collectAsState(initial = vm.cost.collectAsState().value).value
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
      }
    }

  }
}
