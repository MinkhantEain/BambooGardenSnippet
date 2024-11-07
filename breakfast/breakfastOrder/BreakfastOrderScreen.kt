package com.example.bamboogarden.breakfast.breakfastOrder

import android.util.Log
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.example.bamboogarden.R
import com.example.bamboogarden.common.dialogs.loading.LoadingDialog
import com.example.bamboogarden.common.toCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakfastOrderScreen(
    viewModel: BreakfastOrderScreenViewModel,
    onBackClick: () -> Unit,
    onOrderClick: () -> Unit,
) {
    BackHandler {
        viewModel.saveAndBack(onBackClick)
    }
    val table by remember { viewModel.tableState }
    val prices by remember { viewModel.priceState }

    LoadingDialog(controller = viewModel.loadingController)
    Log.d("BreakfastOrderScreen", "BreakfastOrderScreen: ${table.tableId}")
    Box {
        if (!viewModel.validOrderState.value) {
            AlertDialog(
                onDismissRequest = { viewModel.acknowledgeInvalidOrder() },
                confirmButton = {
                    Button(onClick = { viewModel.acknowledgeInvalidOrder() }) { Text("OK") }
                },
                text = { Text("Cannot be lower than the previous cost") }
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = table.tableId,
                            fontSize = TextUnit(40f, TextUnitType.Sp),
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            color = Color.Gray
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { viewModel.saveAndBack(onBackClick) },
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.back_arrow),
                                tint = Color(1,127,161,255),
                                contentDescription =
                                    "BackToBreakfastTableScreenFromBreakfastOrderScreen",
                                modifier = Modifier
                                    .height(50.dp)
                                    .width(50.dp)
                            )
                        }
                    },
                    actions = {
                        TextButton(
                            onClick = { viewModel.saveAndToBill(onOrderClick) }
                        ) {
                            Text(
                                "Bill",
                                fontSize = TextUnit(30f, TextUnitType.Sp),
                                color = Color(53, 181, 219, 255)
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Column(
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .fillMaxHeight(.1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier
                        .height(100.dp)
                        .width(100.dp)) {
                        IconButton(onClick = {
                            viewModel.incrementPeople()
                        }, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Filled.PersonAddAlt1,
                                contentDescription = "Person Icon",
                                modifier = Modifier.fillMaxSize(),
                                tint = Color.Gray
                            )
                        }
                        Text(
                            text = "${table.people}",
                            modifier =
                            Modifier
//                                    .border(1.dp, Color.Black, CircleShape)
                                .align(Alignment.BottomEnd)
                                .height(25.dp)
                                .width(25.dp),
                            textAlign = TextAlign.Center,
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = table.orders.values.fold(0) { i, b -> i + b.totalCost }.toCurrency(),
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = TextUnit(40f, TextUnitType.Sp),
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
                HorizontalDivider(
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxHeight(.85f),
                ) {
                    items(count = prices.count()) { index ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = { viewModel.decrementCount(prices[index]) },
                                Modifier.fillMaxHeight()
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Cloud,
                                    contentDescription = "DecreaseCount",
                                    modifier = Modifier
                                        .height(30.dp)
                                        .width(40.dp)
                                )
                            }
                            Column(
                                Modifier.fillMaxHeight(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    prices[index].toCurrency(),
                                    fontSize = TextUnit(30f, TextUnitType.Sp),
                                    modifier = Modifier.width(100.dp),
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    (table.orders[index.toString()]?.count ?: 0).toString(),
                                    fontSize = TextUnit(20f, TextUnitType.Sp),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                            IconButton(
                                onClick = { viewModel.incrementCount(prices[index]) },
                                Modifier
                                    .fillMaxHeight()
                                    .width(60.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Cloud,
                                    contentDescription = "IncreaseCount",
                                    tint = Color(12, 192, 223, 255),
                                    modifier = Modifier
                                        .height(70.dp)
                                        .width(70.dp),
                                )
                            }
                        }
                    }
                }
                HorizontalDivider(
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                Button(
                    onClick = {
                        viewModel.saveAndBack(onBackClick)
                    },
                    modifier = Modifier
                        .fillMaxWidth(.6f)
                        .height(50.dp),
                    colors =
                        ButtonColors(
                            Color(254, 255, 255, 255),
                            Color(254, 255, 255, 255),
                            Color(254, 255, 255, 255),
                            Color(254, 255, 255, 255)
                        ),
                    elevation = ButtonDefaults.buttonElevation(3.dp, 3.dp, 3.dp, 3.dp, 3.dp)
                ) {
                    Text(
                        text = "Save",
                        fontSize = TextUnit(30f, TextUnitType.Sp),
                        color = Color(51, 173, 196, 255)
                    )
                }
            }
        }
    }
}
