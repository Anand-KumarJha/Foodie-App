package com.example.foodie.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.CartRecyclerAdapter
import com.example.foodie.database.*
import org.json.JSONArray
import org.json.JSONObject


class CartActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerMenu: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: CartRecyclerAdapter
    private lateinit var placeOrderButton: Button
    private var totalPrice: Int = 0

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        placeOrderButton = findViewById(R.id.checkoutButton)
        toolbar = findViewById(R.id.toolbar1)
        recyclerMenu = findViewById(R.id.recyclerMenu1)
        layoutManager = LinearLayoutManager(this)
        setUpToolbar()

        val menuList = RetrieveCartItems(this).execute().get()
        for (i in menuList.indices) {
            totalPrice += (menuList[i].foodPrice).toInt()
        }
        recyclerAdapter = CartRecyclerAdapter(this, menuList)
        recyclerMenu.adapter = recyclerAdapter
        recyclerMenu.layoutManager = layoutManager

        placeOrderButton.text = "Place Order (Rs. $totalPrice)"
        placeOrderButton.setOnClickListener {
            jsonConnector()
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    @Override
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }


    private fun jsonConnector() {
        val queue = Volley.newRequestQueue(this@CartActivity)
        val url = "http://13.235.250.119/v2/place_order/fetch_result/"

        sharedPreferences = getSharedPreferences("Foodie_preference", MODE_PRIVATE)
        val userId = sharedPreferences.getString("User_id", "000")

        val menuList1 = RetrieveCartItems(this).execute().get()

        val jsonParams = JSONObject()

        jsonParams.put("user_id", userId)
        jsonParams.put("restaurant_id", menuList1[0].restaurantId)
        jsonParams.put("total_cost", totalPrice)

        val jsonArray = JSONArray()

        for (element in menuList1) {
            val jsonParams1 = JSONObject()
            jsonParams1.put("food_item_id", element.cart_id)

            jsonArray.put(jsonParams1)
        }
        jsonParams.put("food", jsonArray)
        print("Json Params $jsonParams")

        val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
            Response.Listener {
                print("Response is $it")
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")

                    if (success) {

                        ClearCartItems(this).execute().get()
                        val intent = Intent(this@CartActivity, SuccessOrderActivity::class.java)
                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this@CartActivity,
                            "Sorry order could not be placed!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this@CartActivity,
                        "Unexpected error occurred",
                        Toast.LENGTH_LONG
                    )
                        .show()
                }
            }, Response.ErrorListener {
                Toast.makeText(this@CartActivity, "Unexpected error occurred", Toast.LENGTH_LONG)
                    .show()
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "d9fe1680c644ff"
                return headers
            }

        }
        queue.add(jsonRequest)

    }

    override fun onBackPressed() {
        ClearCartItems(this).execute().get()
        super.onBackPressed()
    }

    class ClearCartItems(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val db = Room.databaseBuilder(context, CartDatabase::class.java, "Cart-Db").build()
            db.cartDao().clearCart()
            db.close()
            return true
        }

    }


    class RetrieveCartItems(val context: Context) : AsyncTask<Void, Void, List<CartEntity>>() {
        override fun doInBackground(vararg params: Void?): List<CartEntity> {
            val db = Room.databaseBuilder(context, CartDatabase::class.java, "Cart-Db").build()
            return db.cartDao().getAllCartItems()
        }

    }

}