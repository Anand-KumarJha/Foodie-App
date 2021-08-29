package model

import com.example.foodie.database.CartEntity

data class OrderHistoryMenu(
    var orderId: String,
    var restaurantName: String,
    var totalPrice: String,
    var date: String,
    var foodMenu: List<CartEntity>
)