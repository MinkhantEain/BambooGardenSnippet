package com.example.bamboogarden

import android.content.Context
import androidx.room.Room
import com.example.bamboogarden.authentication.AuthServiceImpl
import com.example.bamboogarden.breakfast.repository.BreakfastRepositoryImpl
import com.example.bamboogarden.breakfast.repository.LocalBreakfastDatabase
import com.example.bamboogarden.breakfast.repository.LocalBreakfastRepository
import com.example.bamboogarden.chef.repository.ChefRemoteRepository
import com.example.bamboogarden.common.bluetooth.BluetoothController
import com.example.bamboogarden.common.bluetooth.data.AndroidBluetoothController
import com.example.bamboogarden.common.camera.data.CameraDatabase
import com.example.bamboogarden.management.dailyRecord.DailyRecordRepository
import com.example.bamboogarden.management.dailyRecord.DailyRecordRepositoryImpl
import com.example.bamboogarden.management.income.IncomeRepository
import com.example.bamboogarden.management.produceExcel.ExcelRepository
import com.example.bamboogarden.management.produceExcel.ExcelRepositoryImpl
import com.example.bamboogarden.menu.repository.LocalMenuRepoImpl
import com.example.bamboogarden.menu.repository.MenuRepositoryImpl
import com.example.bamboogarden.menu.repository.RemoteMenuRepoImpl
import com.google.firebase.firestore.FirebaseFirestore

interface AppModule {
  val authServiceImpl: AuthServiceImpl
  val remoteMenuRepository: RemoteMenuRepoImpl
  val menuRepository: MenuRepositoryImpl
  val localMenuRepoImpl: LocalMenuRepoImpl
  val chefRemoteRepository: ChefRemoteRepository
  val incomeRepository: IncomeRepository
  val cameraDatabase: CameraDatabase
  val bluetoothController: BluetoothController
  val localBreakfastDatabase: LocalBreakfastDatabase
  val localBreakfastRepository: LocalBreakfastRepository
  val breakfastRepository: BreakfastRepositoryImpl
  val excelRepository: ExcelRepository
  val dailyRecordRepository: DailyRecordRepository
}

class AppModuleImpl(private val appContext: Context) : AppModule {
  override val authServiceImpl: AuthServiceImpl by lazy { AuthServiceImpl() }

  override val bluetoothController: BluetoothController by lazy {
    AndroidBluetoothController(appContext)
  }

  override val remoteMenuRepository: RemoteMenuRepoImpl by lazy { RemoteMenuRepoImpl() }

  override val menuRepository: MenuRepositoryImpl by lazy {
    MenuRepositoryImpl(localMenuRepoImpl, remoteMenuRepository)
  }
  override val localMenuRepoImpl: LocalMenuRepoImpl by lazy { LocalMenuRepoImpl() }

  override val chefRemoteRepository: ChefRemoteRepository by lazy { ChefRemoteRepository() }

  override val incomeRepository: IncomeRepository by lazy { IncomeRepository() }
  override val cameraDatabase: CameraDatabase by lazy {
    Room.databaseBuilder(
      context = BambooGardenApplication.instance.applicationContext,
      CameraDatabase::class.java,
      "CameraDatabase",
    )
      .fallbackToDestructiveMigration()
      .build()
  }

  override val localBreakfastDatabase: LocalBreakfastDatabase by lazy {
    Room.databaseBuilder(
      context = BambooGardenApplication.instance,
      LocalBreakfastDatabase::class.java,
      "LocalBreakfastDatabase"
    ).fallbackToDestructiveMigration()
      .build()
  }
  override val localBreakfastRepository: LocalBreakfastRepository by lazy {
    LocalBreakfastRepository()
  }

  override val breakfastRepository: BreakfastRepositoryImpl by lazy { BreakfastRepositoryImpl() }
  override val excelRepository: ExcelRepository by lazy { ExcelRepositoryImpl(fireStore = FirebaseFirestore.getInstance()) }
  override val dailyRecordRepository: DailyRecordRepository by lazy { DailyRecordRepositoryImpl(firebase = FirebaseFirestore.getInstance()) }
}
