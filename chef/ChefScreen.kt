package com.example.bamboogarden.chef

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.chef.data.DeletedDish
import com.example.bamboogarden.common.viewModelFactory
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChefScreen(
  onBackButtonClick: () -> Unit,
  onHistoryClick: () -> Unit,
) {
  // TODO: arrange orders by time
  val viewModel: ChefScreenViewModel =
    viewModel(
      factory =
      viewModelFactory {
        ChefScreenViewModel(BambooGardenApplication.appModule.chefRemoteRepository)
      }
    )

  val orders by remember { viewModel.orders }

  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "Orders: ${orders.size}") },
        navigationIcon = {
          IconButton(onClick = onBackButtonClick) {
            Icon(
              imageVector = Icons.Filled.ArrowBackIosNew,
              contentDescription = "Back To HomeScreen"
            )
          }
        },
        actions = {
          TextButton(onClick = onHistoryClick) {
            Text(text = "History", fontSize = 18.sp)
          }
        }
      )
    }
  ) {
    LazyColumn(
      modifier =
      Modifier
        .padding(
          top = it.calculateTopPadding(),
          bottom = it.calculateBottomPadding()
        )
        .fillMaxWidth()
    ) {
      items(count = orders.size) { index ->
        val chefOrderWithWrapper = orders[index]
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
//            .border(1.dp, Color.Black, RectangleShape)
        ) {
          Column(modifier = Modifier.fillMaxWidth()) {
            Row(
              modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${chefOrderWithWrapper.tableId} : ${
                  chefOrderWithWrapper.time.substring(
                    0,
                    5
                  )
                }"
              )
              IconButton(
                onClick = { viewModel.completeChefOrder(chefOrderWithWrapper) },
              ) {

                Icon(
                  imageVector = Icons.Filled.CheckCircle,
                  contentDescription = "Mark complete",
                  tint = Color(78, 176, 80, 255),
                  modifier = Modifier.size(35.dp)
                )
              }
            }

            for (dishOrderList in chefOrderWithWrapper.orderList) {
              val dish = dishOrderList.first()
              Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier) {
                IconButton(
                  onClick = {
                    val allOrder = chefOrderWithWrapper.orderList.flatten()
                    val newChefOrder = chefOrderWithWrapper.withoutWrapper()
                      .copy(orderList = allOrder.filter { d -> d.selfRef != dish.selfRef })
                    chefOrderWithWrapper.selfRef.set(newChefOrder).addOnCompleteListener {
                      dish.selfRef.delete()
                      DeletedDish(
                        tableId = chefOrderWithWrapper.tableId,
                        time = LocalTime.now().toString(),
                        dish = dish.dish,
                        selfRef = viewModel.getDeleteDocRef(),
                        date = LocalDate.now().toString(),
                      ).also { deletedDishes ->
                        deletedDishes.selfRef.set(deletedDishes)
                      }
                    }
                  },
                  modifier = Modifier.size(25.dp)
                ) {
                  Icon(
                    imageVector = Icons.Filled.Circle,
                    contentDescription = null,
                    tint = Color.Red,
                    modifier = Modifier.size(8.dp)
                  )
                }
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column {
                    Text(text = dish.dish.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    if (dish.comment.isNotEmpty()) Text(
                      text = "[${dish.comment}]",
                      color = Color(58, 150, 227, 255),
                      fontSize = 16.sp
                    )
                  }
                  Text(
                    text = dishOrderList.count().toString().let { if (it == "1") "" else it},
                    modifier =
                    Modifier
                      .width(25.dp),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
//                    color = Color(1, 127, 162, 255),
//                    fontSize = TextUnit(20f, TextUnitType.Sp)
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}
