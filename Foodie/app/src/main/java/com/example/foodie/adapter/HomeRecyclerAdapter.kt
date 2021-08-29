package com.example.foodie.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodie.R
import com.example.foodie.activity.DescriptionActivity
import com.example.foodie.database.FoodDatabase
import com.example.foodie.database.FoodEntity
import com.example.foodie.fragment.HomeFragment
import com.squareup.picasso.Picasso
import model.Food

class HomeRecyclerAdapter(val context: Context, private val itemList: ArrayList<Food>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.DashboardViewHolder>() {
    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.nameRecyclerRow)
        val price: TextView = view.findViewById(R.id.priceRecyclerRow)
        val rating: TextView = view.findViewById(R.id.rating)
        val favouritesIcon: ImageView = view.findViewById(R.id.favouritesIcon)
        val image: ImageView = view.findViewById(R.id.rowImage)
        val liContent: RelativeLayout = view.findViewById(R.id.liContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_single_row, parent, false)
        return DashboardViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val food = itemList[position]
        holder.name.text = food.foodName
        holder.price.text = "${food.foodPrice}/Person"
        holder.rating.text = food.foodRating
        Picasso.get().load(food.foodImage).error(R.drawable.splash_icon).into(holder.image)

        val foodEntity = FoodEntity(
            food.foodId.toInt(),
            food.foodName,
            food.foodRating,
            food.foodPrice,
            food.foodImage
        )

        val checkFav = DBAsyncTask(context.applicationContext, foodEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            holder.favouritesIcon.setImageResource(R.drawable.ic_action_favourites)
        } else {
            holder.favouritesIcon.setImageResource(R.drawable.ic_action_favourite)
        }


        holder.favouritesIcon.setOnClickListener {

            if (!DBAsyncTask(context.applicationContext, foodEntity, 1).execute().get()) {
                val async = DBAsyncTask(context.applicationContext, foodEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.favouritesIcon.setImageResource(R.drawable.ic_action_favourites)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = DBAsyncTask(context.applicationContext, foodEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        context,
                        "Restaurant removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    holder.favouritesIcon.setImageResource(R.drawable.ic_action_favourite)
                } else {
                    Toast.makeText(
                        context,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        holder.liContent.setOnClickListener {
            val intent = Intent(context, DescriptionActivity::class.java)
            intent.putExtra("name", food.foodName)
            intent.putExtra("id", food.foodId)
            intent.putExtra("price", food.foodPrice)
            intent.putExtra("rating", food.foodRating)
            intent.putExtra("image", food.foodImage)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class DBAsyncTask(val context: Context, val foodEntity: FoodEntity, private val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        //Mode 1: Check DB that food is favourite or not
        //Mode 2: Add to favourite
        //Mode 3: Remove from favourites

        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, FoodDatabase::class.java, "Food-Db").build()

            when (mode) {
                1 -> {
                    val food: FoodEntity? = db.foodDao().getFoodById(foodEntity.food_id.toString())
                    db.close()
                    return food != null
                }
                2 -> {
                    db.foodDao().insertFood(foodEntity)
                    return true
                }
                3 -> {
                    db.foodDao().deleteFood(foodEntity)
                    return true
                }
                4 -> {
                    db.foodDao().clearFavourites()
                }
            }
            return false
        }
    }
}