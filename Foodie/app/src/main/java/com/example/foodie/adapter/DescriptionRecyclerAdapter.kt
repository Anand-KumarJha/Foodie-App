package com.example.foodie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodie.R
import com.example.foodie.database.CartDatabase
import com.example.foodie.database.CartEntity
import model.FoodMenu

class DescriptionRecyclerAdapter(val context: Context, private val itemList: ArrayList<FoodMenu>) :
    RecyclerView.Adapter<DescriptionRecyclerAdapter.DescriptionViewHolder>() {

    class DescriptionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var rowCount: TextView = view.findViewById(R.id.rowCount)
        var itemName: TextView = view.findViewById(R.id.itemName)
        var itemPrice: TextView = view.findViewById(R.id.itemPrice)
        var add: Button = view.findViewById(R.id.btnAdd)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.menu_item_row, parent, false)
        return DescriptionViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        holder.rowCount.text = (position + 1).toString()
        holder.itemName.text = itemList[position].itemName
        holder.itemPrice.text = "Rs. ${itemList[position].itemPrice}"

        val cartEntity = CartEntity(
            itemList[position].itemId,
            itemList[position].restaurantId,
            itemList[position].restaurantName,
            itemList[position].itemName,
            itemList[position].itemPrice
        )

        val checkItem =
            DescriptionRecyclerAdapter.DBAsyncTask1(context.applicationContext, cartEntity, 1)
                .execute()
        val isItemPresent = checkItem.get()

        if (isItemPresent) {
            holder.add.text = "Remove"
            holder.add.setBackgroundColor(Color.parseColor("#FF9800"))
        } else {
            holder.add.text = "Add"
            holder.add.setBackgroundColor(Color.parseColor("#FF5722"))
        }

        holder.add.setOnClickListener {
            if (holder.add.text.toString() == "Add") {
                holder.add.text = "Remove"
                holder.add.setBackgroundColor(Color.parseColor("#FF9800"))
            } else {
                holder.add.text = "Add"
                holder.add.setBackgroundColor(Color.parseColor("#FF5722"))
            }
        }

        holder.add.setOnClickListener {

            if (!DescriptionRecyclerAdapter.DBAsyncTask1(context.applicationContext, cartEntity, 1)
                    .execute().get()
            ) {
                val async = DescriptionRecyclerAdapter.DBAsyncTask1(
                    context.applicationContext,
                    cartEntity,
                    2
                ).execute()
                val result = async.get()
                if (result) {
                    holder.add.text = "Remove"
                    holder.add.setBackgroundColor(Color.parseColor("#FF9800"))
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = DescriptionRecyclerAdapter.DBAsyncTask1(
                    context.applicationContext,
                    cartEntity,
                    3
                ).execute()
                val result = async.get()
                if (result) {
                    holder.add.text = "Add"
                    holder.add.setBackgroundColor(Color.parseColor("#FF5722"))
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    class DBAsyncTask1(val context: Context, val cartEntity: CartEntity, private val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        //Mode 1: Check DB that food is favourite or not
        //Mode 2: Add to favourite
        //Mode 3: Remove from favourites

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, CartDatabase::class.java, "Cart-Db").build()

            when (mode) {
                1 -> {
                    val cart: CartEntity? = db.cartDao().getCartById(cartEntity.cart_id.toString())
                    db.close()
                    return cart != null
                }
                2 -> {
                    db.cartDao().insertFood(cartEntity)
                    return true
                }
                3 -> {
                    db.cartDao().deleteFood(cartEntity)
                    return true
                }
            }
            return false
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}