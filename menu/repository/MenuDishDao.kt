package com.example.bamboogarden.menu.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.bamboogarden.menu.data.MenuDish

@Dao
interface MenuDishDao {

  @Upsert
  suspend fun upsertMenuDish(menuDish: MenuDish)

  @Upsert
  suspend fun upsertMenuDishes(menuDishes: List<MenuDish>)

  @Delete
  suspend fun deleteMenuDish(menuDish: MenuDish)

  @Query("select * from MenuDish order by id ASC")
  suspend fun getMenuDishOrderByID() : List<MenuDish>

  @Query("select * from MenuDish where name like '%' || :userInput || '%' or acronym like '%' || :userInput || '%' ")
  suspend fun getDishMenuContains(userInput: String): List<MenuDish>

  @Query("select * from MenuDish where type like :chosenType")
  suspend fun getDishMenuOfType(chosenType: String) : List<MenuDish>

  @Query("select * from MenuDish where popular is 1")
  suspend fun getPopularMenuDishes() : List<MenuDish>

  @Query("Select * from MenuDish")
  suspend fun getAllDishes(): List<MenuDish>;
}
