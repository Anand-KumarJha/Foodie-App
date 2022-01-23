package com.example.foodie.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import org.json.JSONObject

class Page1_1 : AppCompatActivity() {

    lateinit var phone: EditText
    private lateinit var email: EditText
    private lateinit var btnNext: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page11)
        btnNext = findViewById(R.id.login_button)

        btnNext.setOnClickListener {

            phone = findViewById(R.id.phone)
            email = findViewById(R.id.email)

            val queue = Volley.newRequestQueue(this@Page1_1)
            val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", phone.text.toString())
            jsonParams.put("email", email.text.toString())

            val phone1 = phone.text.toString()
            print(jsonParams)

            when {
                phone.text.length != 10 -> {
                    Toast.makeText(
                        this,
                        "Please enter your 10 digit mobile number",
                        Toast.LENGTH_LONG
                    ).show()
                }
                !(email.text.contains("@") && email.text.length > 5 && email.text.contains(".com")) -> {
                    Toast.makeText(this, "Please enter valid email", Toast.LENGTH_LONG).show()
                }
                else -> {
                    val jsonRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                            Response.Listener {

                                try {
                                    val data = it.getJSONObject("data")
                                    val success = data.getBoolean("success")

                                    print("Response is $it")

                                    if (success) {

                                        val intent = Intent(this@Page1_1, Page1_1_1::class.java)
                                        intent.putExtra("phone", phone1)

                                        startActivity(intent)
                                        finish()

                                    } else {
                                        Toast.makeText(
                                            this@Page1_1, "Email or phone is not registered",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    Toast.makeText(
                                        this@Page1_1,
                                        "Unexpected error occurred",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }, Response.ErrorListener {
                                Toast.makeText(
                                    this@Page1_1,
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
}