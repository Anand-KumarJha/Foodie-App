package com.example.foodie.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.adapter.HomeRecyclerAdapter
import com.example.foodie.util.ConnectionManager
import model.Food
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var sorted = false
    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    var foodInfoList = arrayListOf<Food>()
    private var priceComparator = Comparator<Food> { food1, food2 ->
        food1.foodPrice.compareTo(food2.foodPrice, true)
    }

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
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)

        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.filter) {

            sorted = if (!sorted) {
                Collections.sort(foodInfoList, priceComparator)
                true
            } else {
                Collections.sort(foodInfoList, priceComparator)
                foodInfoList.reverse()
                false
            }
        }
        recyclerAdapter.notifyDataSetChanged()
        return true
    }

    fun sendToRecycler() {
        recyclerAdapter =
            HomeRecyclerAdapter(activity as Context, foodInfoList)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager
    }

    private fun jsonConnector() {
        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"

        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        progressLayout.visibility = View.GONE

                        if (success) {
                            val datas = data.getJSONArray("data")
                            for (i in 0 until datas.length()) {
                                val foodJsonObject = datas.getJSONObject(i)
                                val foodObject = Food(
                                    foodJsonObject.getString("id"),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("rating"),
                                    foodJsonObject.getString("cost_for_one"),
                                    foodJsonObject.getString("image_url")
                                )
                                foodInfoList.add(foodObject)
                                sendToRecycler()
                            }
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Some error occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Error Occurred",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    if (activity != null) {
                        Toast.makeText(
                            activity as Context,
                            "Internet error occurred! Please restart app with active internet",
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