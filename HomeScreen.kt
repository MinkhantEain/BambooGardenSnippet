
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bamboogarden.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
  onLogoutButtonClick: () -> Unit,
  onBreakfastButtonClick: () -> Unit,
  onMenuButtonClick: () -> Unit,
  onChefButtonClick: () -> Unit,
  onManagementButtonClick: () -> Unit,
  onBluetoothButtonClick: () -> Unit,
) {
  val vm: HomeScreenViewModel = viewModel()
  val TAG: String = "HomeScreen"

  val enableBluetoothLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.StartActivityForResult()
    ) {}
  val permissionLauncher =
    rememberLauncherForActivityResult(
      contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { perms ->
      val canEnableBluetooth =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          perms[Manifest.permission.BLUETOOTH_CONNECT] == true
        } else true

      if (canEnableBluetooth && !vm.isBluetoothEnabled()) {
        enableBluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
      }
      Log.d(
        TAG,
        "canEnableBluetooth: $canEnableBluetooth, bluetoothEnabled: ${vm.isBluetoothEnabled()}"
      )
    }

  LaunchedEffect(key1 = true) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      permissionLauncher.launch(
        arrayOf(
          Manifest.permission.BLUETOOTH_CONNECT,
          Manifest.permission.BLUETOOTH_SCAN,
          Manifest.permission.ACCESS_BACKGROUND_LOCATION,
          Manifest.permission.BLUETOOTH_ADMIN,
        )
      )
    }
  }

  Scaffold(
    contentWindowInsets = ScaffoldDefaults.contentWindowInsets.add(WindowInsets(top = 10.dp)),
    topBar = {
      TopAppBar(
        title = {
          Text(
            text = "Bamboo Garden",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          )
        },
        actions = {
          TextButton(
            onClick = {
              vm.signOut()
              onLogoutButtonClick()
            }
          ) {
            Text(text = "Log out", fontWeight = FontWeight.Bold)
          }
        }
      )
    },
  ) { paddingValues ->
    Column(
      modifier =
      Modifier
        .fillMaxSize()
        .padding(
          top = paddingValues.calculateTopPadding(),
          start = 40.dp,
          end = 40.dp,
          bottom = paddingValues.calculateBottomPadding()
        )
        .verticalScroll(rememberScrollState()),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      MainScreenButton("Breakfast", color = Color(255, 250, 159, 255)) { onBreakfastButtonClick() }

      MainScreenButton("Menu", color = Color(255, 227, 92, 255)) { onMenuButtonClick() }

      MainScreenButton("Chef View") { onChefButtonClick() }

      MainScreenButton("Management") {
        onManagementButtonClick()
      }

//      MainScreenButton("File dish price Import") {
//        val collection = FirebaseFirestore.getInstance().collection("NewMenuDishes")
//        GlobalScope.launch(Dispatchers.IO) {
//          BambooGardenApplication.instance.assets.open("dishes.txt").bufferedReader().let { br ->
//            while (true) {
//              val line = br.readLine() ?: break
//              val token = line.split(',')
//              val id = token[0]
//              val name = token[1]
//              val price = runCatching { Integer.parseInt(token[2]) }.getOrNull() ?: continue
//              val acronym = token[3]
//              val type = token[4]
//              val popular = token[5] == "TRUE"
//              val dish = MenuDish(id, name, price, type, acronym, popular)
//              collection.document(dish.id).set(dish)
//              Log.d(TAG, "HomeScreen: $dish")
//            }
//            br.close()
//          }
//        }
//      }

      MainScreenButton("Bluetooth") {
        onBluetoothButtonClick()
      }
    }
  }
}

@Composable
fun MainScreenButton(
  text: String,
  color: Color = Color(242, 241, 246, 255),
  onClick: () -> Unit,
) {
  Box(modifier = Modifier) {
    ElevatedButton(
      onClick = { onClick() },
      modifier = Modifier
        .padding(vertical = 30.dp)
        .fillMaxWidth()
        .height(60.dp),
      colors =
      ButtonColors(
        color,
        color,
        color,
        color,
      )
    ) {
      Text(
        text,
        fontSize = TextUnit(25f, TextUnitType.Sp),
        textAlign = TextAlign.Start,
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 10.dp),
        color = Color.Black
      )
    }
  }
}
