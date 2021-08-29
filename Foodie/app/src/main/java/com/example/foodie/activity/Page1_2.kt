package com.example.foodie.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import org.json.JSONObject

class Page1_2 : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var mobile: EditText
    private lateinit var address: EditText
    private lateinit var newPassword: EditText

    lateinit var userIdR: String
    lateinit var nameR: String
    lateinit var emailR: String
    lateinit var mobileR: String
    lateinit var addressR: String

    private lateinit var btnRegister: Button
    private lateinit var imageView2: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page12)

        sharedPreferences = getSharedPreferences("Foodie_preference", MODE_PRIVATE)

        name = findViewById(R.id.name)
        email = findViewById(R.id.email)
        mobile = findViewById(R.id.mobile)
        address = findViewById(R.id.delivery_address)
        newPassword = findViewById(R.id.create_password)

        btnRegister = findViewById(R.id.login_button)
        imageView2 = findViewById(R.id.imageView2)

        managePageConnections()

        val queue = Volley.newRequestQueue(this@Page1_2)
        val url = "http://13.235.250.119/v2/register/fetch_result"

        btnRegister.setOnClickListener {

            when {
                name.text.length <= 2 -> {
                    Toast.makeText(
                        this,
                        "Name should contain 3 or mare characters",
                        Toast.LENGTH_LONG
                    ).show()
                }
                !(email.text.contains("@") && email.text.length > 5 && email.text.contains(".com")) -> {
                    Toast.makeText(this, "Please enter valid email", Toast.LENGTH_LONG).show()
                }
                mobile.text.length != 10 -> {
                    Toast.makeText(
                        this,
                        "Please enter your 10 digit mobile number",
                        Toast.LENGTH_LONG
                    ).show()
                }
                address.text.length < 3 -> {
                    Toast.makeText(this, "Please enter valid address", Toast.LENGTH_LONG).show()
                }
                newPassword.text.length <= 2 -> {
                    Toast.makeText(
                        this,
                        "Minimum Length of Password Should Be 3",
                        Toast.LENGTH_LONG
                    ).show()
                }

                else -> {
                    val jsonParams = JSONObject()
                    jsonParams.put("name", name.text.toString())
                    jsonParams.put("mobile_number", mobile.text.toString())
                    jsonParams.put("password", newPassword.text.toString())
                    jsonParams.put("address", address.text.toString())
                    jsonParams.put("email", email.text.toString())

                    val jsonRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
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

                                        sharedPreferences.edit().putBoolean("isLoggedIn", true)
                                            .apply()
                                        sharedPreferences.edit().putString("User_id", userIdR).apply()
                                        sharedPreferences.edit().putString("Name", nameR).apply()
                                        sharedPreferences.edit().putString("Mobile", mobileR)
                                            .apply()
                                        sharedPreferences.edit().putString("Address", addressR)
                                            .apply()
                                        sharedPreferences.edit().putString("Email", emailR).apply()

                                        Toast.makeText(
                                            this@Page1_2,
                                            "Welcome ${name.text}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        val intent = Intent(this@Page1_2, Page2::class.java)

                                        startActivity(intent)
                                        finish()


                                    } else {
                                        Toast.makeText(
                                            this@Page1_2,
                                            "Mobile number OR Email Id is already registered",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(
                                        this@Page1_2,
                                        "Unexpected error occurred",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@Page1_2,
                                    "Unexpected error occurred",
                                    Toast.LENGTH_LONG
                                ).show()
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


            }
        }


    }


    private fun managePageConnections() {

        imageView2.setOnClickListener {
            val intent = Intent(this@Page1_2, Page1::class.java)
            finish()
            startActivity(intent)
        }
    }
}