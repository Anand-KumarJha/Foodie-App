package com.example.foodie.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.OrderHistoryRecyclerAdapter
import com.example.foodie.database.CartEntity
import com.example.foodie.util.ConnectionManager
import model.OrderHistoryMenu
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderHistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderHistoryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: OrderHistoryRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var noOrderHistoryScreen: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)

        recyclerHome = view.findViewById(R.id.recyclerHome1)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        noOrderHistoryScreen = view.findViewById(R.id.noOrderHistoryScreen)
        noOrderHistoryScreen.visibility = View.VISIBLE
        progressLayout.visibility = View.VISIBLE

        jsonConnector()
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderHistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderHistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun jsonConnector() {
        sharedPreferences = this.requireActivity().getSharedPreferences(
            "Foodie_preference",
            AppCompatActivity.MODE_PRIVATE
        )
        val userId = sharedPreferences.getString("User_id", "000")
        println(userId)
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/orders/fetch_result/$userId"

        val foodInfoList = arrayListOf<OrderHistoryMenu>()

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    println("The new response is $it")
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        println(success)

                        progressLayout.visibility = View.GONE

                        if (success) {
                            println("Success but .....")
                            val datas = data.getJSONArray("data")
                            for (i in 0 until datas.length()) {
                                val foodJsonObject = datas.getJSONObject(i)
                                val itemsObject = foodJsonObject.getJSONArray("food_items")

                                val foodMenu = mutableListOf<CartEntity>()
                                lateinit var foodId1: String
                                lateinit var foodName: String
                                lateinit var foodCost: String
                                for (j in 0 until itemsObject.length()) {
                                    foodId1 = itemsObject.getJSONObject(j).getString("food_item_id")
                                    foodName = itemsObject.getJSONObject(j).getString("name")
                                    foodCost = itemsObject.getJSONObject(j).getString("cost")

                                    foodMenu.add(
                                        CartEntity(
                                            foodId1,
                                            foodJsonObject.getString("order_id"),
                                            foodJsonObject.getString("restaurant_name"),
                                            foodName,
                                            foodCost
                                        )
                                    )
                                }
                                val date =
                                    foodJsonObject.getString("order_placed_at").subSequence(0, 8)
                                val foodObject = OrderHistoryMenu(
                                    foodJsonObject.getString("order_id"),
                                    foodJsonObject.getString("restaurant_name"),
                                    foodJsonObject.getString("total_cost"),
                                    date.toString(),
                                    foodMenu
                                )
                                foodInfoList.add(foodObject)
                                progressLayout.visibility = View.GONE
                                noOrderHistoryScreen.visibility = View.GONE
                                recyclerAdapter =
                                    OrderHistoryRecyclerAdapter(activity as Context, foodInfoList)
                                recyclerHome.adapter = recyclerAdapter
                                recyclerHome.layoutManager = layoutManager
                            }
                        } else {
                            noOrderHistoryScreen.visibility = View.VISIBLE
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Catch Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()

                        e.printStackTrace()
                    }

                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Volley error occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }
}