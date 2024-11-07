package com.example.bamboogarden.breakfast.breakfastOrder

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bamboogarden.breakfast.data.BreakfastOrder
import com.example.bamboogarden.breakfast.data.Table
import com.example.bamboogarden.breakfast.repository.BreakfastRepositoryImpl
import com.example.bamboogarden.common.dialogs.DialogController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await

class BreakfastOrderScreenViewModel(
    private val repo: BreakfastRepositoryImpl,
    private val tableId: String,
) : ViewModel() {
    private val _tableState = mutableStateOf(Table())
    private val _priceState = mutableStateOf<List<Int>>(listOf())
    private val _initialTotalCostState = mutableIntStateOf(0)
    val tableState: State<Table>
        get() = _tableState

    val priceState: State<List<Int>>
        get() = _priceState

    private val _validOrderState = mutableStateOf(true)
    val validOrderState: State<Boolean> = _validOrderState

    val loadingController = DialogController()

    init {
        loadingController.show()
        viewModelScope.launch {
            repo.syncDatabases()
            initTable()
            _priceState.value = repo.getPrices()
        }.invokeOnCompletion { loadingController.hide() }
    }

    fun acknowledgeInvalidOrder() {
        _validOrderState.value = true
    }

    override fun onCleared() {
        onTableClear()
        super.onCleared()
    }

    private fun initTable() {
        viewModelScope.launch {
            repo.getTable(tableId).let {
                if (it.people == 0) _tableState.value = it.copy(people = 1)
                else _tableState.value = it
            }
            _initialTotalCostState.intValue = _tableState.value.getTotalCost()
            repo.togglePresence(_tableState.value)
        }
    }

    private fun onTableClear() {
        if (_tableState.value.orders.values.isEmpty()) {
            _tableState.value = _tableState.value.copy(people = 0)
            runBlocking { repo.togglePresence(_tableState.value) }
            Log.d("BreakfastOrderScreenViewModel", "onTableClear: ${_tableState.value}")
        }
    }

    fun incrementCount(price: Int) {
        val priceIndex = _priceState.value.indexOfFirst { it == price }
        val mp = _tableState.value.orders.toMutableMap()
        val temp = mp[priceIndex.toString()]
        mp[priceIndex.toString()] =
            BreakfastOrder(
                temp?.price ?: price,
                (temp?.count?.plus(1)) ?: 1,
            )
        Log.d(
            "BreakfastOrderViewModel",
            "incrementCount: ${mp[priceIndex.toString()]}, price: $price\n" +
                "priceIndex: $priceIndex, prices:${priceState.value}"
        )
        _tableState.value = _tableState.value.copy(orders = mp)
    }

    fun decrementCount(price: Int) {
        val priceIndex = _priceState.value.indexOfFirst { it == price }
        val mp = _tableState.value.orders.toMutableMap()
        val temp = mp[priceIndex.toString()]
        mp[priceIndex.toString()] =
            BreakfastOrder(
                temp?.price ?: price,
                ((temp?.count?.minus(1)) ?: 0).let { if (it >= 0) return@let it else 0 },
            )
        _tableState.value = _tableState.value.copy(orders = mp)
    }

    private suspend fun saveOrder() {
        Log.d("BreakfastOrderVM", "orderTable: ${tableState.value}")
        Log.d(
            "BreakfastOrderScreenViewModel",
            "orderTable: ${tableState.value.getTotalCost()} ${_tableState.value.getTotalCost()} ${_initialTotalCostState.intValue}"
        )
        if (tableState.value.getTotalCost() < _initialTotalCostState.intValue) {
            Log.d("BreakfastOrderScreenViewModel", "orderTable: invalid order")
            _validOrderState.value = false
            return
        } else {
            if (tableState.value.orders.values.fold(0) { i, b -> i + b.totalCost } != 0) {
                tableState.value.selfRef.set(tableState.value).await()
            }
        }
        repo.logBreakfastSave(tableState.value)
    }

    fun saveAndBack(backClick: () -> Unit) {
        loadingController.show()
        viewModelScope.launch(Dispatchers.IO) {
            saveOrder()
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                loadingController.hide()
                backClick()
            }
        }
    }

    fun saveAndToBill(callBack: () -> Unit) {
        loadingController.show()
        viewModelScope.launch(Dispatchers.IO) {
            saveOrder()
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.Main) {
                loadingController.hide()
                callBack()
            }
        }
    }

    fun incrementPeople() {
        _tableState.value = _tableState.value.copy(people = _tableState.value.people + 1)
    }
}
