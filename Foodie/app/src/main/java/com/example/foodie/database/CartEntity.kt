package com.example.foodie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
data class CartEntity(
    @PrimaryKey val cart_id: String,
    @ColumnInfo(name = "restaurant_id") val restaurantId: String,
    @ColumnInfo(name = "restaurant_name") val restaurantName: String,
    @ColumnInfo(name = "food_name") val foodName: String,
    @ColumnInfo(name = "food_price") val foodPrice: String
)