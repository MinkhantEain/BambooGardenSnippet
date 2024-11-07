package com.example.bamboogarden.menu.menuTable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.R
import com.example.bamboogarden.menu.data.MenuTable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTablesScreen(
  onBackButtonClick: () -> Unit,
  onTableClick: (table: MenuTable) -> Unit,
  onHistoryClick: () -> Unit,
) {
  val viewModel: MenuTableScreenViewModel = viewModel()
  val tables = remember { viewModel.tables }
  Scaffold(
    topBar = {
      TopAppBar(
        title = { Text(text = "Menu Table") },
        navigationIcon = {
          IconButton(onClick = onBackButtonClick) {
            Icon(
              painter = painterResource(id = R.drawable.back_arrow),
              tint = Color(1,127,161,255),
              contentDescription = "Back to Home Screen"
            )
          }
        },
        actions = {
          TextButton(onClick = onHistoryClick) {
            Text(text = "History", fontSize = TextUnit(18f, TextUnitType.Sp))
          }
        }
      )
    }
  ) {
    LazyVerticalGrid(
      columns = GridCells.FixedSize(80.dp),
      verticalArrangement = Arrangement.SpaceBetween,
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier
        .padding(top = it.calculateTopPadding())
        .fillMaxHeight(0.9f)
    ) {
      items(items = tables) { table ->
        ElevatedButton(
          onClick = {
            viewModel.menuTableTogglePresence(table.copy(present = true))
            onTableClick(table)
          },
          shape = RoundedCornerShape(10),
          modifier = Modifier.size(80.dp),
        ) {
          Text(
            text = table.tableId,
            fontSize = TextUnit(18f, TextUnitType.Sp),
            textAlign = TextAlign.Center,
            color =
            if (!table.present) Color(134, 134, 134, 255)
            else Color(58,150,227,255),
            fontWeight = FontWeight.Bold,
          )
        }
      }
    }
  }
}
