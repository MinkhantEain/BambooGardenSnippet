package com.example.bamboogarden.common

import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate

val prefix: String get() = if (FirebaseAuth.getInstance().currentUser!!.uid == "IFQXYhAwvjeP4FiulbiFqw88EG23") "" else "Test"

val BREAKFASTCUSTOMERCOLLECTION: String get() = "${prefix}NewBreakfast"
const val MENUDISHESCOLLECTION = "NewMenuDishes"
const val APPLICATIONMENUDISHES = "ApplicationData/MenuDishes"
val CHEFCOLLECTION get() = "${prefix}NewChef"

val BREAKFAST_SAVE_LOG get() = "${prefix}BreakfastSaveLog"

fun INCOMECOLLECTION(date: LocalDate = LocalDate.now()) : String{
  return "Accounting/${prefix}Income/${date}"
}

fun EXPENSECOLLECTION(date: LocalDate = LocalDate.now()) : String{
  return "Accounting/${prefix}Expense/${date}"
}

val CHEF_DELETE_COLLECTION get() = "${prefix}ChefDelete"

val DAILYRECORDDOCUMENTREFERENCE get() = "Accounting/${prefix}DailyRecord"

fun MENUPAYMENTCOLLECTION(date: LocalDate = LocalDate.now()) : String {
  return "Payments/${date}/${prefix}MenuPayment"
}
fun DAILYRECORDOFDATE(date: LocalDate = LocalDate.now()) : String {
  return "${prefix}DailyRecord/${date}"
}
fun BREAKFASTPAYMENTCOLLECTION(date: LocalDate = LocalDate.now()): String =
  "Payments/$date/${prefix}BreakfastPayment"

val MENUTABLECOLLECTION get() = "${prefix}NewMenu"
const val BREAKFASTORDERPRICES: String = "Breakfast/Order"
const val PRICEFIELD: String = "Price"

const val BREAKFASTDATA: String = "ApplicationData/Breakfast"
const val CARRYONFIELD:String = "carryOn"
const val LASTMODIFIEDFIELD:String = "lastModified"
const val ACCOUNTEDFIELD:String = "accounted"

const val ORDERPERSONNELFIELD : String = "orderPersonnel"
const val ORDERSFIELD : String = "orders"
const val PRESENTFIELD : String = "present"
const val ISOCCUPIEDFIELD : String = "isOccupied"
const val PEOPLEFIELD : String = "people"

const val BREAKFASTORDERSCREENTABLEKEY : String = "BreakfastOrderScreenTableKey"

const val TABLEIDKEY = "TableIDKey"

val MENUPAYMENTCOLLECTIONGROUP : String get() = "${prefix}MenuPayment"
val BREAKFASTPAYMENTCOLLECTIONGROUP: String get ()= "${prefix}BreakfastPayment"


fun ORDEREDCOLLECTION(tableId: String, ) : String = "$MENUTABLECOLLECTION/$tableId/Ordered"

const val SSPUUID = "00001101-0000-1000-8000-00805F9B34FB"