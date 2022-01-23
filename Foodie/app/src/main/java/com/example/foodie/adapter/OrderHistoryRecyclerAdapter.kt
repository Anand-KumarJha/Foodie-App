package com.example.foodie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.foodie.R
import model.OrderHistoryMenu


class OrderHistoryRecyclerAdapter(
    val context: Context,
    private val itemList: ArrayList<OrderHistoryMenu>
) : RecyclerView.Adapter<OrderHistoryRecyclerAdapter.OrderHistoryViewHolder>() {

    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        var recyclerHome: RecyclerView = view.findViewById(R.id.recyclerRecyclerView)
        lateinit var layoutManager: RecyclerView.LayoutManager
        lateinit var recyclerAdapter: CartRecyclerAdapter
        val restaurantName: TextView = view.findViewById(R.id.restaurantName)
        val restaurantDate: TextView = view.findViewById(R.id.orderDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.order_history_item_row, parent, false)
        return OrderHistoryRecyclerAdapter.OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderHistoryViewHolder, position: Int) {
        holder.restaurantName.text = itemList[position].restaurantName
        holder.restaurantDate.text = itemList[position].date
        val foodMenu = itemList[position].foodMenu

        holder.layoutManager = LinearLayoutManager(context)
        holder.recyclerAdapter =
            CartRecyclerAdapter(context, foodMenu)
        holder.recyclerHome.adapter = holder.recyclerAdapter
        holder.recyclerHome.layoutManager = holder.layoutManager

    }

    override fun getItemCount(): Int {
        return itemList.size
    }


}