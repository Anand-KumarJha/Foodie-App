package model

data class CartItems(
    val restaurantId: String,
    val restaurantName: String,
    val foodId: String,
    val foodMenu: FoodMenu
)