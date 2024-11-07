package com.example.bamboogarden.breakfast.breakfastTable

import android.graphics.drawable.PaintDrawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.R
import com.example.bamboogarden.breakfast.data.Table
import com.example.bamboogarden.common.viewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakfastTableScreen(
    viewModel: BreakfastTableScreenViewModel =
        viewModel<BreakfastTableScreenViewModel>(
            factory =
                viewModelFactory { extras ->
                    BreakfastTableScreenViewModel(
                        savedStateHandle = extras.createSavedStateHandle(),
                        repo = BambooGardenApplication.appModule.breakfastRepository
                    )
                }
        ),
    onBackToHomeClick: () -> Unit,
    onTableClicked: (Table, BreakfastTableScreenViewModel) -> Unit,
    onHistoryButtonClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Breakfast",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackToHomeClick) {
                        
                        Icon(
                            painter = painterResource(id = R.drawable.back_arrow),
                            contentDescription = "Exit from BreakfastTableScreen",
                            modifier = Modifier
                                .width(50.dp)
                                .height(50.dp),
                            tint = Color(1,127,161,255)
                        )
                    }
                },
                actions = {
                    TextButton(onClick = onHistoryButtonClick) {
                        Text(
                            "History",
                            fontSize = TextUnit(18f, TextUnitType.Sp),
                            color = Color.Black
                        )
                    }
                }
            )
        }
    ) {
        if (viewModel.tables.isEmpty())
            Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.Center))
            }
        else {
            LazyVerticalGrid(
                columns = GridCells.FixedSize(80.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = it,
            ) {
                items(
                    viewModel.tables,
                    key = { table -> table.tableId },
                ) { table ->
                    Box(
                        modifier =
                        Modifier
                            .height(80.dp)
                            .background(
                                color =
                                if (table.people > 0) Color(254, 255, 255, 255)
                                else Color(242, 241, 246, 255),
                                shape = CircleShape
                            )
                            .shadow(
                                shape = CircleShape,
                                elevation = .0001.dp,
                                clip = true,
                            )
                            .border(
                                width = if (table.people > 0) 2.dp else 1.dp,
                                color =
                                if (table.people > 0) Color(69, 163, 189, 255)
                                else Color(223, 223, 223, 255),
                                CircleShape
                            )
                            .clickable {
                                Log.d("BreakfastTableScreen", "BreakfastTableScreen: $table")
                                onTableClicked(table, viewModel)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = table.tableId,
                            fontSize = TextUnit(30F, TextUnitType.Sp),
                            color =
                                if (table.people > 0) Color(92, 196, 223, 255)
                                else Color(158,156,159,255),
                            fontWeight = FontWeight.W500
                        )
                    }
                }
            }
        }
    }
}
