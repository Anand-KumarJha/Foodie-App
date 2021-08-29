package com.example.foodie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.DescriptionRecyclerAdapter
import com.example.foodie.adapter.HomeRecyclerAdapter
import com.example.foodie.database.FoodEntity
import com.example.foodie.util.ConnectionManager
import model.FoodMenu
import org.json.JSONException

class DescriptionActivity : AppCompatActivity() {
    private var descriptionActivityGetter: Activity? = null

    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var recyclerMenu: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: DescriptionRecyclerAdapter
    private lateinit var favouritesIcon: ImageView
    private lateinit var btnGoToCart: Button
    private lateinit var restaurantId: String
    lateinit var progressLayout: RelativeLayout

    val menuList = arrayListOf<FoodMenu>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        descriptionActivityGetter = this
        toolbar = findViewById(R.id.toolbar)
        recyclerMenu = findViewById(R.id.recyclerMenu)
        layoutManager = LinearLayoutManager(this)
        favouritesIcon = findViewById(R.id.favouritesIcon)
        btnGoToCart = findViewById(R.id.goToCart)
        progressLayout = findViewById(R.id.progressLayout)

        progressLayout.visibility = View.VISIBLE

        restaurantId = intent.getStringExtra("id").toString()
        setToolbar()


        val foodEntity = FoodEntity(
            restaurantId.toInt(),
            intent.getStringExtra("name").toString(),
            intent.getStringExtra("rating").toString(),
            intent.getStringExtra("price").toString(),
            intent.getStringExtra("image").toString()
        )

        val checkFav = HomeRecyclerAdapter.DBAsyncTask(applicationContext, foodEntity, 1).execute()
        val isFav = checkFav.get()

        if (isFav) {
            favouritesIcon.setImageResource(R.drawable.ic_action_favourites)
        } else {
            favouritesIcon.setImageResource(R.drawable.ic_action_favourite)
        }
        favouritesIcon.setOnClickListener {

            if (!HomeRecyclerAdapter.DBAsyncTask(applicationContext, foodEntity, 1).execute()
                    .get()
            ) {
                val async = HomeRecyclerAdapter.DBAsyncTask(
                    applicationContext,
                    foodEntity,
                    2
                ).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this,
                        "Restaurant added to favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    favouritesIcon.setImageResource(R.drawable.ic_action_favourites)
                } else {
                    Toast.makeText(
                        this,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                val async = HomeRecyclerAdapter.DBAsyncTask(
                    applicationContext,
                    foodEntity,
                    3
                ).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(
                        this,
                        "Restaurant removed from favourites",
                        Toast.LENGTH_SHORT
                    ).show()
                    favouritesIcon.setImageResource(R.drawable.ic_action_favourite)
                } else {
                    Toast.makeText(
                        this,
                        "Some error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        jsonConnector()

        btnGoToCart.setOnClickListener {
            val menuList = CartActivity.RetrieveCartItems(this).execute().get()
            if (menuList.isNotEmpty()) {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    this,
                    "Please select items to proceed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = intent.getStringExtra("name")
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
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

        if (ConnectionManager().checkConnectivity(this)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {

                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            progressLayout.visibility = View.GONE
                            val datas = data.getJSONArray("data")

                            for (i in 0 until datas.length()) {
                                val foodJsonObject = datas.getJSONObject(i)

                                val foodObject = FoodMenu(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("restaurant_id"),
                                    intent.getStringExtra("name").toString(),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one")
                                )
                                menuList.add(foodObject)
                                sendToRecycler()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Some error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this,
                            "Some Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        this,
                        "Volley error occurred!",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "d9fe1680c644ff"

                        return headers
                    }
                }
            queue.add(jsonObjectRequest)

        } else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                this.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    fun sendToRecycler() {
        recyclerAdapter = DescriptionRecyclerAdapter(this, menuList)
        recyclerMenu.adapter = recyclerAdapter
        recyclerMenu.layoutManager = layoutManager
    }

    override fun onBackPressed() {
        CartActivity.ClearCartItems(this).execute().get()
        super.onBackPressed()
    }


}