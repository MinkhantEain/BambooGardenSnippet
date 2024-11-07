package com.example.bamboogarden.menu.repository

import com.example.bamboogarden.common.APPLICATIONMENUDISHES
import com.example.bamboogarden.common.CHEFCOLLECTION
import com.example.bamboogarden.common.MENUDISHESCOLLECTION
import com.example.bamboogarden.common.MENUPAYMENTCOLLECTION
import com.example.bamboogarden.common.MENUTABLECOLLECTION
import com.example.bamboogarden.common.ORDEREDCOLLECTION
import com.example.bamboogarden.common.PRESENTFIELD
import com.example.bamboogarden.menu.data.ChefOrder
import com.example.bamboogarden.menu.data.DishOrder
import com.example.bamboogarden.menu.data.DishOrderStatus
import com.example.bamboogarden.menu.data.MenuDish
import com.example.bamboogarden.menu.data.MenuDishData
import com.example.bamboogarden.menu.data.MenuPayment
import com.example.bamboogarden.menu.data.MenuPaymentWrapper
import com.example.bamboogarden.menu.data.toMenuPayment
import com.example.bamboogarden.menu.menuOrder.MenuOrderWrapper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime

class RemoteMenuRepoImpl {
  val firebase = FirebaseFirestore.getInstance()

  fun getTablesCollection(): CollectionReference {
    return firebase.collection(MENUTABLECOLLECTION)
  }

  suspend fun getMenuDishData(): MenuDishData {
    return withContext(Dispatchers.IO) {
      return@withContext firebase
        .document(APPLICATIONMENUDISHES)
        .get()
        .await()
        .toObject<MenuDishData>()!!
    }
  }

  suspend fun getMenuDishes(): List<MenuDish> {
    return withContext(Dispatchers.IO) {
      return@withContext firebase.collection(MENUDISHESCOLLECTION).get().await().documents.map {
        it.toObject<MenuDish>()!!
      }
    }
  }

  fun getOrderedDishCollection(tableId: String): CollectionReference {
    return firebase.collection(ORDEREDCOLLECTION(tableId))
  }

  suspend fun selectMenuDish(tableId: String, dishOrder: DishOrder, comment: String) {
    withContext(Dispatchers.IO) {
      val selfRef = firebase.collection(ORDEREDCOLLECTION(tableId)).document()
      selfRef.set(dishOrder.copy(selfRef = selfRef, comment = comment)).await()
    }
  }

  suspend fun sendDishOrdersToChef(
    tableId: String,
    menuOrderWrappers: List<MenuOrderWrapper>,
    batch: WriteBatch?
  ) {
    withContext(Dispatchers.IO) {
      val doc = firebase.collection(CHEFCOLLECTION).document()
      val dishOrders: MutableList<DishOrder> = mutableListOf()
      menuOrderWrappers.forEach { dishOrders.addAll(it.dishList) }
      if (dishOrders.isNotEmpty()) {
        val chefOrder = ChefOrder(LocalTime.now().toString(), doc, dishOrders, tableId)
        if (batch == null) {
          chefOrder.selfRef.set(chefOrder).await()
        } else {
          batch.set(chefOrder.selfRef, chefOrder)
        }
      }
    }
  }

  private suspend fun setDishOrderStatusToPaid(
    menuOrderWrappers: List<MenuOrderWrapper>,
    batch: WriteBatch
  ) {
    withContext(Dispatchers.IO) {
      menuOrderWrappers.forEach { menuOrderWrapper ->
        menuOrderWrapper.dishList.forEach { dishOrder ->
          batch.set(dishOrder.selfRef, dishOrder.copy(status = DishOrderStatus.Paid))
        }
      }
    }
  }

  suspend fun collectPayment(
    tableId: String,
    menuOrderWrappers: List<MenuOrderWrapper>,
    batch: WriteBatch,
    taxIncluded: Boolean,
    serviceChargeIncluded: Boolean,
  ) {
    withContext(Dispatchers.IO) {
      setDishOrderStatusToPaid(menuOrderWrappers, batch)

      val paymentWrappers = menuOrderWrappers.toMenuPayment()

      val docRef = firebase.collection(MENUPAYMENTCOLLECTION()).document()
      batch.set(
        docRef,
        MenuPayment(
          time = LocalTime.now().toString(),
          date = LocalDate.now().toString(),
          wrappers = paymentWrappers,
          cost = paymentWrappers.fold(0) { acc: Int, menuPaymentWrapper: MenuPaymentWrapper ->
            acc + menuPaymentWrapper.totalCost
          },
          tableId = tableId,
          selfRef = docRef,
        ).includeTax(taxIncluded).includeServiceCharge(serviceChargeIncluded)
      )
    }
  }

  fun getCollection(path: String): CollectionReference {
    return firebase.collection(path)
  }

  suspend fun updateTablePresence(tableId: String) {
    return withContext(Dispatchers.IO) {
      val present =
        firebase.collection(ORDEREDCOLLECTION(tableId)).get().await().documents.size != 0
      firebase.document("$MENUTABLECOLLECTION/$tableId").update(PRESENTFIELD, present).await()
    }
  }
}
