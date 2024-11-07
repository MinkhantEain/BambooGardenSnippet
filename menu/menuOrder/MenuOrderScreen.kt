package com.example.bamboogarden.menu.menuOrder

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.R
import com.example.bamboogarden.common.LoadingOverlay
import com.example.bamboogarden.common.viewModelFactory
import com.example.bamboogarden.menu.data.DishOrderStatus
import com.example.bamboogarden.menu.menuCommon.colorOnStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOrderScreen(
    tableId: String,
    onBackButtonClick: () -> Unit,
    onConfirmButtonClick: () -> Unit,
) {
    val viewModel: MenuOrderScreenViewModel =
        viewModel(
            factory =
                viewModelFactory {
                    MenuOrderScreenViewModel(
                        tableId = tableId,
                        menuRepositoryImpl = BambooGardenApplication.appModule.menuRepository
                    )
                }
        )
    val backClick = {
        viewModel.updatePresence()
        onBackButtonClick()
    }
    BackHandler {
        backClick()
    }
    Box {
        if (viewModel.isLoading.value) {
            LoadingOverlay()
        }
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = tableId) },
                    navigationIcon = {
                        IconButton(onClick = backClick) {
                            Icon(
                                painter = painterResource(id = R.drawable.back_arrow),
                                tint = Color(1,127,161,255),
                                contentDescription = "Icon Back To Menu Selection"
                            )
                        }
                    },
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(.85f)
                ) {
                    items(
                        count = viewModel.menuOrderWrappers.value.size,
                        key = { index ->
                            val dishOrder =
                                viewModel.menuOrderWrappers.value[index].dishList.first()
                            dishOrder.dish.id + dishOrder.status.name + dishOrder.comment
                        }
                    ) { index ->
                        val menuOrderWrapper = viewModel.menuOrderWrappers.value[index]
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.width(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                IconButton(
                                    onClick = { viewModel.deleteDish(menuOrderWrapper) },
                                    modifier = Modifier.size(20.dp),
                                    enabled =
                                    menuOrderWrapper.dishList.first().status ==
                                      DishOrderStatus.Deciding,
                                ) {
                                    if (
                                        menuOrderWrapper.dishList.first().status ==
                                        DishOrderStatus.Deciding
                                    )
                                        Icon(
                                            imageVector = Icons.Filled.Remove,
                                            contentDescription = "Remove Dish IconButton",
                                            tint = Color(1, 127, 161, 255),
                                            modifier = Modifier.height(30.dp)
                                        )
                                }
                            }
                            Text(
                                menuOrderWrapper.dishList.size.toString(),
                                modifier = Modifier.width(45.dp),
                                fontSize = TextUnit(18f, TextUnitType.Sp),
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                            Column {
                                val fontSize = TextUnit(20f, TextUnitType.Sp)
                                Text(
                                    menuOrderWrapper.dishList.first().dish.name,
                                    color = colorOnStatus(menuOrderWrapper.dishList.first().status),
                                    fontSize = fontSize
                                )
                                if (menuOrderWrapper.dishList.first().comment.isNotEmpty()) {
                                    Text(
                                        "(${menuOrderWrapper.dishList.first().comment})",
                                        fontSize = fontSize
                                    )
                                }
                            }
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.height(10.dp), color = Color.Black)
                ElevatedButton(
                    onClick = {
                        viewModel.confirmOrder() {
                            onConfirmButtonClick()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(.5f)
                        .height(50.dp),
                    colors =
                    ButtonColors(
                        Color.White,
                        Color.White,
                        Color.White,
                        Color.White,
                    )
                ) {
                    Text(
                        "Order",
                        fontSize = TextUnit(30f, TextUnitType.Sp),
                        color = Color(1, 127, 161, 255)
                    )
                }
            }
        }
    }
}
