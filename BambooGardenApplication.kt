package com.example.bamboogarden

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class BambooGardenApplication : Application() {
  companion object {
    lateinit var appModule: AppModule
    lateinit var instance: BambooGardenApplication


  }

  override fun onCreate() {
    super.onCreate()
    instance = this
    appModule = AppModuleImpl(this.applicationContext)
  }
}
