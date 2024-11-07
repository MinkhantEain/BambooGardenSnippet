package com.example.bamboogarden.breakfast.breakfastBill

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.bamboogarden.R
import com.example.bamboogarden.breakfast.data.Table
import com.example.bamboogarden.common.dialogs.loading.ProgressiveLoadingDialog
import com.example.bamboogarden.common.toCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakfastBillScreen(
    tableState: State<Table>,
    onBackClick: () -> Unit,
    onChangeClick: () -> Unit,
    onPaymentClick: (Int) -> Unit,
    vm :BreakfastBillScreenViewModel,
) {
    ProgressiveLoadingDialog(controller = vm.loadingController)
    val table by remember { tableState }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            contentDescription = "Back to BreakfastTableScreen",
                            painter = painterResource(id = R.drawable.back_arrow),
                            tint = Color(1,127,161,255)
                        )
                    }
                },
                actions = {
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Person Icon",
                        modifier = Modifier
                            .height(60.dp)
                            .width(60.dp)
                    )
                    Text(
                        text = table.people.toString(),
                        fontSize = TextUnit(30f, TextUnitType.Sp),
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(end = 30.dp)
                    )
                }
            )
        },
    ) {
        val total =
            table.orders.values.fold(0) { acc, breakfastOrder -> acc + breakfastOrder.totalCost }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier =
                Modifier
                    .height(100.dp)
                    .width(100.dp)
                    .border(2.dp, Color(134, 134, 134, 255), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = table.tableId,
                    style = TextStyle(Color(12, 192, 223, 255)),
                    fontSize = TextUnit(50f, TextUnitType.Sp),
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Box(modifier = Modifier.height(15.dp))
            for (b in table.orders.values) {
                Row(
                    modifier = Modifier.fillMaxWidth(0.8f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = b.price.toCurrency(),
                        fontSize = TextUnit(25f, TextUnitType.Sp),
                        modifier = Modifier.width(70.dp),
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "${b.count}",
                        fontSize = TextUnit(25f, TextUnitType.Sp),
                        modifier = Modifier.width(35.dp),
                        textAlign = TextAlign.Start
                    )
                }
            }
            Box(modifier = Modifier.fillMaxHeight(0.05f))
            ElevatedButton(
                onClick = onChangeClick,
                colors =
                    ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.White,
                        disabledContentColor = Color.White,
                        disabledContainerColor = Color.White
                    ),
              modifier = Modifier.fillMaxWidth(.3f)
            ) {
                Text(
                    "Add+",
                    fontSize = TextUnit(25f, TextUnitType.Sp),
                    color = Color(12, 192, 223, 255)
                )
            }
            Box(modifier = Modifier.height(10.dp))
            HorizontalDivider(modifier = Modifier.padding(bottom = 30.dp),
              color = Color(51,47,48,255), thickness = .5.dp)
            Row(
                modifier = Modifier.fillMaxWidth(0.85f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    "Total",
                    fontSize = TextUnit(35f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                  total.toCurrency(),
                    fontSize = TextUnit(35f, TextUnitType.Sp),
                    fontWeight = FontWeight.Bold
                )
            }
            Box(modifier = Modifier.height(70.dp))
            ElevatedButton(
                onClick = { onPaymentClick(total) },
                colors =
                    ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(9,187,56,255),
                        disabledContainerColor = Color.LightGray,
                        disabledContentColor = Color.Red
                    ),
              modifier = Modifier.fillMaxWidth(.55f)
            ) {
                Text(text = "Collect", fontSize = TextUnit(30f, TextUnitType.Sp))
            }
        }
    }
}
