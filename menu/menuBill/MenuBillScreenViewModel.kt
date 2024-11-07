package com.example.bamboogarden.menu.menuBill

import android.annotation.SuppressLint
import android.bluetooth.BluetoothManager
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.BambooGardenApplication
import com.example.bamboogarden.common.InternetConnection
import com.example.bamboogarden.common.dialogs.errorDialog.ErrorDialogController
import com.example.bamboogarden.common.receiptPrinter.ThermalPrinter
import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.data.DishOrderStatus
import com.example.bamboogarden.menu.menuOrder.MenuOrderWrapper
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MenuBillScreenViewModel(
  private val tableId: String,
  private val repo: MenuRepositoryImpl,
  private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
  companion object {
    const val COST = "Cost"
    const val HAS_SERVICE_CHARGE = "hasServiceCharge"
    const val HAS_TAX = "hasTax"
  }

  private val TAG = "MenuBillScreenViewModel"
  private val printer = ThermalPrinter.getInstance()

  private val _allPaid = mutableStateOf(false)
  val allPaid: State<Boolean> = _allPaid

  private val _wrappers = mutableStateOf<List<MenuOrderWrapper>>(listOf())
  val wrapper: State<List<MenuOrderWrapper>> = _wrappers


  val cost = savedStateHandle.getStateFlow(COST, 0)

  val hasServiceCharge = savedStateHandle.getStateFlow(HAS_SERVICE_CHARGE, true)
  val hasTax = savedStateHandle.getStateFlow(HAS_TAX, true)

  val serviceCharge = combine(hasServiceCharge, cost) { predicate, cost ->
    if (predicate) cost.times(0.05).toInt()
    else 0
  }

  val tax = combine(hasTax, cost) { predicate, cost ->
    if (predicate) cost.times(0.05).toInt()
    else 0
  }

  val totalCost = combine(tax, serviceCharge, cost) { tax, serviceCharge, cost ->
    tax + serviceCharge + cost
  }


  private val _paymentCollectable = mutableStateOf(false)
  val paymentCollectable: State<Boolean> = _paymentCollectable

  private val _billClearable = mutableStateOf(false)

  val showBluetoothDeviceSelectionPage = mutableStateOf(false)


  private var _onBitmapCreated = MutableLiveData<Bitmap?>(null)
  var onBitmapGenerated: LiveData<Bitmap?> = _onBitmapCreated

  val isLoading = mutableStateOf(false)

  private lateinit var listenerRegistration: ListenerRegistration

  val isInitialised = mutableStateOf(false)

  val errorDialogController = ErrorDialogController()

  fun bitmapCreated(bitmap: Bitmap?) {
    _onBitmapCreated.value = bitmap
  }

  init {
    subscribeToDishOrder()
  }

  fun toggle(string: String) {
    savedStateHandle[string] = savedStateHandle.get<Boolean>(string)?.not()
  }

  override fun onCleared() {
    if (this::listenerRegistration.isInitialized) listenerRegistration.remove()
    super.onCleared()
  }

  @SuppressLint("MissingPermission")
  fun isBluetoothConnected(): Boolean {
    val bluetoothManager =
      BambooGardenApplication.instance.getSystemService(BluetoothManager::class.java)
    val bluetoothAdapter = bluetoothManager.adapter
    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
      val pairedDevices = bluetoothAdapter.bondedDevices
      for (device in pairedDevices) {
        if (device.bondState == android.bluetooth.BluetoothDevice.BOND_BONDED) {
          try {
            val method = device.javaClass.getMethod("isConnected")
            if (method.invoke(device) as Boolean) {
              return true
            }
          } catch (e: Exception) {
            e.printStackTrace()
          }
        }
      }
    }
    return false
  }

  fun printBill(bitmap: Bitmap) {
    isLoading.value = true
    if (!printer.isConnectedToPrinterServiceSocket()) {
      isLoading.value = false
      showBluetoothDeviceSelectionPage.value = true
      return
    }
    viewModelScope.launch {
      try {
        printer.printImageReceipt(bitmap)
      } catch (e: Exception) {
        isLoading.value = false
        errorDialogController.showError(
          "Bluetooth Printer Error",
          e.message ?: "error without message"
        )
      }
    }.invokeOnCompletion {
      isLoading.value = false
    }
  }

  fun updatePresence() {
    viewModelScope.launch { repo.updateTablePresence(tableId) }
  }

  private fun subscribeToDishOrder() {
    viewModelScope.launch(Dispatchers.IO) {
      listenerRegistration =
        repo.getOrderedDishCollection(tableId).addSnapshotListener { querySnapshot, error ->
          error?.let { Log.d(TAG, "subscribeToDishOrder: ${error.message}") }

          querySnapshot?.let { snapshot ->
            val temp = mutableListOf<MenuOrderWrapper>()
            val groupings =
              snapshot.documents
                .map { it.toObject<DishOrder>()!! }
                .groupBy { it.dish.id + it.status.name }
            groupings.values.forEach {
              temp.add(MenuOrderWrapper(it.size, it.first().status, it))
            }
            _wrappers.value = temp
            savedStateHandle[COST] =
              temp.fold(0) { acc: Int, menuOrderWrapper: MenuOrderWrapper ->
                acc +
                  menuOrderWrapper.count *
                  menuOrderWrapper.dishList.first().dish.price
              }
            _allPaid.value = temp.fold(true) { acc: Boolean, menuOrderWrapper: MenuOrderWrapper ->
              acc && (menuOrderWrapper.dishList.first().status ==
                DishOrderStatus.Paid)
            }
            _paymentCollectable.value =
              temp.fold(true) { acc: Boolean, menuOrderWrapper: MenuOrderWrapper ->
                acc &&
                  (menuOrderWrapper.dishList.first().status ==
                    DishOrderStatus.Completed ||
                    menuOrderWrapper.dishList.first().status ==
                    DishOrderStatus.Paid)
              } &&
                temp.isNotEmpty() &&
                temp.any { it.dishList.first().status == DishOrderStatus.Completed }

            _billClearable.value =
              temp.fold(true) { acc: Boolean, menuOrderWrapper: MenuOrderWrapper ->
                acc &&
                  (menuOrderWrapper.dishList.first().status == DishOrderStatus.Paid)
              } && temp.isNotEmpty()
          }
        }
    }.invokeOnCompletion {
      isInitialised.value = true
    }
  }

  fun collectPayment() {
    isLoading.value = true
    viewModelScope.launch(Dispatchers.IO) {
      val batch = FirebaseFirestore.getInstance().batch()
      val payableGrouping =
        _wrappers.value.groupBy { it.dishList.first().status == DishOrderStatus.Completed }
      payableGrouping[true]?.let {
        repo.collectPayment(
          tableId,
          it,
          batch = batch,
          taxIncluded = hasTax.value,
          serviceChargeIncluded = hasServiceCharge.value
        )
      }
      if (!InternetConnection.hasNetworkConnection()) {
        errorDialogController.showError(
          title = "No internet access",
          text = "There is no internet access. Connect to internet and try again"
        )
        return@launch
      } else {
        batch.commit()
      }
    }.invokeOnCompletion {
      isLoading.value = false
    }
  }

  fun clearBill(onCompleteCallback: () -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      val batch = FirebaseFirestore.getInstance().batch()
      val clearableGroup =
        _wrappers.value.groupBy { it.dishList.first().status == DishOrderStatus.Paid }
      clearableGroup[true]?.let {
        it.forEach { menuOrderWrapper ->
          menuOrderWrapper.dishList.forEach { dishOrder ->
            batch.delete(dishOrder.selfRef)
          }
        }
      }
      batch.commit().addOnCompleteListener { onCompleteCallback() }
    }
  }
}
