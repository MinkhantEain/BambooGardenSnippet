package com.example.bamboogarden.common.bluetooth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bamboogarden.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(
  state: BluetoothUiState,
  onStartScan: () -> Unit,
  onStopScan: () -> Unit,
  onDeviceClick: (BluetoothDeviceDomain) -> Unit,
  onDisconnectClick: () -> Unit,
  connectedDevice: BluetoothDevice?,
  onBackClick: () -> Unit
) {
  Scaffold(
    topBar = {
      TopAppBar(title = {},
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(
              painter = painterResource(id = R.drawable.back_arrow),
              tint = Color(1, 127, 161, 255),
              contentDescription = "Back to Home Screen"
            )
          }
        })
    }
  ) { paddings ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddings)
    ) {
      BluetoothDeviceList(
        connectedDevice = connectedDevice,
        pairedDevices = state.pairedDevices,
        scannedDevices = state.scannedDevices,
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f),
        onClick = onDeviceClick
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Button(onClick = onStartScan) { Text(text = "Start scan") }
        Button(onClick = onStopScan) { Text(text = "Stop scan") }
        Button(onClick = onDisconnectClick) { Text(text = "Disconnect") }

      }
    }
  }
}

@Composable
fun BluetoothDeviceList(
  modifier: Modifier = Modifier,
  pairedDevices: List<BluetoothDevice>,
  scannedDevices: List<BluetoothDevice>,
  connectedDevice: BluetoothDevice?,
  onClick: (BluetoothDevice) -> Unit,
) {
  LazyColumn(modifier = modifier) {
    item {
      Text(
        text = "Paired Devices",
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        modifier = Modifier.padding(16.dp)
      )
    }

    items(pairedDevices) { device ->
      Row(verticalAlignment = Alignment.CenterVertically) {
        if (connectedDevice == device)
          Icon(
            imageVector = Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = Color(78, 176, 80, 255)
          )
        Text(
          text = device.name ?: "no name",
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(device) }
            .padding(16.dp)
        )
      }

    }

    item {
      Text(
        text = "Scanned Devices",
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        modifier = Modifier.padding(16.dp)
      )
    }

    items(scannedDevices) { device ->
      Text(
        text = device.name ?: "no name",
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onClick(device) }
          .padding(16.dp)
      )
    }
  }
}
