package com.example.foodie.activity


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import org.json.JSONObject

class Page1 : AppCompatActivity() {
    private lateinit var phoneNo: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var forgotPassword: TextView
    private lateinit var register: TextView
    lateinit var sharedPreference: SharedPreferences

    lateinit var userIdR: String
    lateinit var nameR: String
    lateinit var emailR: String
    lateinit var mobileR: String
    lateinit var addressR: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page1)

        sharedPreference = getSharedPreferences("Foodie_preference", MODE_PRIVATE)
        val isLoggedIn = sharedPreference.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            val intent = Intent(this@Page1, Page2::class.java)
            startActivity(intent)
            finish()
        }

        loginButton = findViewById(R.id.login_button)
        forgotPassword = findViewById(R.id.et_forgot_password)
        register = findViewById(R.id.et_Registor)

        loginButton.setOnClickListener {
            phoneNo = findViewById(R.id.editTextTextPersonName)
            password = findViewById(R.id.editTextTextPassword)

            val queue = Volley.newRequestQueue(this@Page1)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()

            jsonParams.put("mobile_number", phoneNo.text.toString())
            jsonParams.put("password", password.text.toString())


            val jsonRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                Response.Listener {
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val userJsonObject = data.getJSONObject("data")

                            userIdR = userJsonObject.getString("user_id")
                            nameR = userJsonObject.getString("name")
                            mobileR = userJsonObject.getString("mobile_number")
                            addressR = userJsonObject.getString("address")
                            emailR = userJsonObject.getString("email")

                            sharedPreference.edit().putBoolean("isLoggedIn", true).apply()
                            sharedPreference.edit().putString("User_id", userIdR).apply()
                            sharedPreference.edit().putString("Name", nameR).apply()
                            sharedPreference.edit().putString("Mobile", mobileR).apply()
                            sharedPreference.edit().putString("Address", addressR).apply()
                            sharedPreference.edit().putString("Email", emailR).apply()

                            Toast.makeText(this@Page1, "Welcome $nameR", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@Page1, Page2::class.java)

                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@Page1,
                                "Please Enter Valid Login Details!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this@Page1, "Unexpected error occurred", Toast.LENGTH_LONG)
                            .show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@Page1, "Unexpected error occurred", Toast.LENGTH_LONG)
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

        forgotPassword.setOnClickListener {
            val intent = Intent(this@Page1, Page1_1::class.java)
            startActivity(intent)
        }

        register.setOnClickListener {
            val intent = Intent(this@Page1, Page1_2::class.java)
            startActivity(intent)
        }

    }

}