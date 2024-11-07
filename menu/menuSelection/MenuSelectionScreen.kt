package com.example.bamboogarden.menu.menuSelection

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.R
import com.example.bamboogarden.common.toCurrency
import com.example.bamboogarden.common.viewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuSelectionScreen(
  tableId: String,
  onBackButtonClick: () -> Unit = {},
  onCheckButtonClick: () -> Unit,
) {
  val viewModel: MenuSelectionScreenViewModel =
    viewModel(
      factory =
      viewModelFactory {
        MenuSelectionScreenViewModel(
          menuRepository = BambooGardenApplication.appModule.menuRepository,
          tableId = tableId
        )
      }
    )

  BackHandler {
    viewModel.updateTablePresence()
    onBackButtonClick()
  }

  CommentDialog(commentDialogController = viewModel.commentDialogController)

  ModalNavigationDrawer(
    drawerContent = {
      ModalDrawerSheet(modifier = Modifier.fillMaxWidth(0.4f)) {
        LazyColumn {
          item {
            TextButton(onClick = {
              viewModel.getPopularMenuDishes()
            }, modifier = Modifier.fillMaxWidth()) {
              Text(text = "Popular")
            }
          }
          items(
            count = viewModel.categoryList.value.size,
            key = { index -> viewModel.categoryList.value[index] }
          ) { index ->
            TextButton(
              onClick = {
                viewModel.getFilteredMenuDishByType(
                  viewModel.categoryList.value[index]
                )
              },
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(text = viewModel.categoryList.value[index])
            }
          }
        }
      }
    }
  ) {
    Scaffold(
      topBar = {
        TopAppBar(
          title = { Text(text = "${tableId}: ${viewModel.selectedDishes.value.size}") },
          navigationIcon = {
            IconButton(onClick = {
              viewModel.updateTablePresence()
              onBackButtonClick()
            }) {
              Icon(
                painter = painterResource(id = R.drawable.back_arrow),
                tint = Color(1,127,161,255),
                contentDescription = "Back To MenuTableScreen"
              )
            }
          },
          actions = {
            TextButton(onClick = onCheckButtonClick) {
              Text(
                text = "Check",
                fontSize = TextUnit(25f, TextUnitType.Sp),
                color = Color(1, 127, 162, 255)
              )
            }
          }
        )
      },
    ) { paddings ->
      CustomSearchBar(
        customerSearchInput = viewModel.customerSearchInput,
        onSearch = { s -> viewModel.searchFilter(s) },
        modifier = Modifier.padding(top = (paddings.calculateTopPadding().times(0.5f)))
      )
      LazyColumn(
        modifier = Modifier
          .padding(top = paddings.calculateTopPadding() * 1.5f)
          .fillMaxWidth()
      ) {
        items(
          count = viewModel.menuDishes.value.size,
          key = { index -> viewModel.menuDishes.value[index].id }
        ) { index ->
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 10.dp, vertical = 5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(
              onClick = {
                viewModel.commentDialogController.value.show.value = true
                viewModel.commentDialogController.value.confirmationCallBack.value =
                  {
                    val menuDish = viewModel.menuDishes.value[index]
                    viewModel.selectMenuDish(
                      menuDish,
                      viewModel.commentDialogController.value.userInput.value
                    )
                    viewModel.clearCommentDialogController()
                  }
              },
              modifier = Modifier.size(30.dp)
            ) {
              Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Add Comment to Dish",
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
              )
            }
            Text(
              text =
              viewModel.selectedDishes.value
                .count { dishOrder ->
                  dishOrder.dish.id == viewModel.menuDishes.value[index].id
                }
                .let { if (it > 0) it.toString() else "" },
              modifier =
              Modifier
                .width(25.dp),

              textAlign = TextAlign.Center,
              color = Color(1, 127, 162, 255),
              fontWeight = FontWeight.Bold,
              fontSize = TextUnit(20f, TextUnitType.Sp)
            )
            Text(
              text = viewModel.menuDishes.value[index].name,
              fontSize = TextUnit(18f, TextUnitType.Sp),
              overflow = TextOverflow.Ellipsis,
              textAlign = TextAlign.Start,
              modifier =
              Modifier
                .fillMaxWidth(.85f)

                .clickable {
                  val menuDish = viewModel.menuDishes.value[index]
                  viewModel.selectMenuDish(menuDish, "")
                },
            )
            Text(
              text = viewModel.menuDishes.value[index].price.toCurrency(),
              modifier =
              Modifier.width(50.dp),
              color = Color(183, 175, 175, 255),
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              textAlign = TextAlign.End
            )

          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
  customerSearchInput: MutableState<String>,
  onSearch: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  SearchBar(
    modifier = modifier.padding(horizontal = 10.dp),
    leadingIcon = {
      Icon(imageVector = Icons.Filled.Search, contentDescription = "Search Bar Leading Icon")
    },
    query = customerSearchInput.value,
    placeholder = { Text("Search...") },
    shape = CircleShape,
    onQueryChange = { s: String ->
      customerSearchInput.value = s
      onSearch(s)
    },
    onSearch = onSearch,
    active = false,
    trailingIcon = { IconButton(onClick = {
      customerSearchInput.value = ""
    }) {
      Icon(imageVector = Icons.Filled.Close, contentDescription = null, tint = Color.Black)
    }},
    onActiveChange = {}
  ) {}
}

@Composable
fun CommentDialog(commentDialogController: State<CommentDialogController>) {
  if (commentDialogController.value.show.value) {
    AlertDialog(
      onDismissRequest = {
        commentDialogController.value.show.value = false
        commentDialogController.value.userInput.value = ""
        commentDialogController.value.confirmationCallBack.value = {}
      },
      confirmButton = {
        TextButton(onClick = commentDialogController.value.confirmationCallBack.value) {
          Text(text = "OK")
        }
      },
      title = { Text(text = "ဟင်းပွဲမှတ်ချက်") },
      text = {
        TextField(
          value = commentDialogController.value.userInput.value,
          onValueChange = { s -> commentDialogController.value.userInput.value = s }
        )
      },
    )
  }
}
