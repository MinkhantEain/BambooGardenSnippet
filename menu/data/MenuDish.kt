package com.example.bamboogarden.menu.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "MenuDish")
data class MenuDish(
    @PrimaryKey val id: String = "",
    @ColumnInfo("name") val name: String = "",
    @ColumnInfo("price") val price: Int = 0,
    @ColumnInfo("type") val type: String = "",
    @ColumnInfo("acronym") val acronym: String = "",
    @ColumnInfo("popular") val popular: Boolean = false,
)
