package com.example.bamboogarden.menu.repository

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.bamboogarden.menu.data.MenuDishData

@Dao
interface MenuDishDataDao {
  @Upsert
  suspend fun updateMenuDishData(menuDishData: MenuDishData)

  @Query("select * from MenuDishData where id=0 limit 1")
  suspend fun getMenuDishData(): MenuDishData
}
