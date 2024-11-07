package com.example.bamboogarden.breakfast.data

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Table(
    val tableId: String = "",
    val orderPersonnel: String = "",
    val orders: Map<String, BreakfastOrder> = mapOf(),
    val people: Int = 0,
    val selfRef: DocumentReference = FirebaseFirestore.getInstance().document(""),
) {
    fun toBreakfastPayment(collectorId: String): BreakfastPayment {
        return BreakfastPayment(
            collectorId = collectorId,
            orders = orders,
            tableId = tableId,
            totalCost = orders.values.fold(0) { acc, b -> acc + b.totalCost },
            time = LocalTime.now().toString(),
            date = LocalDate.now().toString(),
            people = people
        )
    }

    fun getTotalCost(): Int {
        return orders.values.fold(0) { acc, b -> acc + b.totalCost }
    }

    fun toTableLog(dateTime: LocalDateTime): TableLog {
        return TableLog(
            tableId,
            orderPersonnel,
            orders,
            people,
            selfRef,
            dateTime.toString(),
        )
    }
}
