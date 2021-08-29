package com.example.foodie.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodie.R
import com.example.foodie.adapter.HomeRecyclerAdapter
import com.example.foodie.database.FoodDatabase
import com.example.foodie.database.FoodEntity
import model.Food

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavouriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavouriteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerHome: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerAdapter: HomeRecyclerAdapter
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private var foodInfoList = listOf<FoodEntity>()

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
        val view = inflater.inflate(R.layout.fragment_favourite, container, false)
        recyclerHome = view.findViewById(R.id.recyclerFavourite)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE
        var foodList = arrayListOf<Food>()
        foodInfoList = RetrieveFavourites(activity as Context).execute().get()
        for (i in foodInfoList.indices){
            foodList.add(
                Food(
                    foodInfoList[i].food_id.toString(),
                    foodInfoList[i].foodName.toString(),
                    foodInfoList[i].foodRating.toString(),
                    foodInfoList[i].foodPrice.toString(),
                    foodInfoList[i].foodImage.toString()
                )
            )
        }

        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter =
                HomeRecyclerAdapter(activity as Context, foodList)
            recyclerHome.adapter = recyclerAdapter
            recyclerHome.layoutManager = layoutManager
        }

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavouriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavouriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    class RetrieveFavourites(val context: Context) : AsyncTask<Void, Void, List<FoodEntity>>() {
        override fun doInBackground(vararg params: Void?): List<FoodEntity> {
            val db = Room.databaseBuilder(context, FoodDatabase::class.java, "Food-Db").build()
            return db.foodDao().getAllFoods()
        }

    }
}