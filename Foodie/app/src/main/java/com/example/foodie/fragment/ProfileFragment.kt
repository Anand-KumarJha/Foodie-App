package com.example.foodie.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.foodie.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var name1: TextView
    private lateinit var email: TextView
    private lateinit var mobile: TextView
    private lateinit var address: TextView

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
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        sharedPreferences = this.requireActivity().getSharedPreferences(
            "Foodie_preference",
            AppCompatActivity.MODE_PRIVATE
        )

        name1 = view.findViewById(R.id.user_name)
        mobile = view.findViewById(R.id.user_phone)
        address = view.findViewById(R.id.user_address)
        email = view.findViewById(R.id.user_email)

        val nameR = sharedPreferences.getString("Name", "User")
        val mobileR = sharedPreferences.getString("Mobile", "User")
        val addressR = sharedPreferences.getString("Address", "User")
        val emailR = sharedPreferences.getString("Email", "User")

        name1.text = nameR.toString()
        mobile.text = mobileR.toString()
        address.text = addressR.toString()
        email.text = emailR.toString()

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}