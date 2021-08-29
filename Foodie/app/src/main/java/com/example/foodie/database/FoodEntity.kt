package com.example.foodie.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "food")
data class FoodEntity(
    @PrimaryKey val food_id: Int,
    @ColumnInfo(name = "food_name") val foodName: String,
    @ColumnInfo(name = "food_rating") val foodRating: String,
    @ColumnInfo(name = "food_price") val foodPrice: String,
    @ColumnInfo(name = "food_image") val foodImage: String
)