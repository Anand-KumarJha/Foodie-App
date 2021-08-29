package com.example.foodie.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CartDao {
    @Insert
    fun insertFood(cartEntity: CartEntity)

    @Delete
    fun deleteFood(cartEntity: CartEntity)

    @Query("SELECT * FROM cart")
    fun getAllCartItems(): List<CartEntity>

    @Query("SELECT * FROM cart WHERE cart_id = :cartId")
    fun getCartById(cartId: String): CartEntity

    @Query("DELETE FROM cart")
    fun clearCart()
}