package com.example.foodie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.R
import com.example.foodie.database.CartEntity

class CartRecyclerAdapter(context: Context, private val itemList: List<CartEntity>) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var itemName: TextView = view.findViewById(R.id.nameRecyclerRow)
        var itemPrice: TextView = view.findViewById(R.id.priceRecyclerRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.cart_item_row, parent, false)
        return CartViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.itemName.text = itemList[position].foodName
        holder.itemPrice.text = "Rs. ${itemList[position].foodPrice}"
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}