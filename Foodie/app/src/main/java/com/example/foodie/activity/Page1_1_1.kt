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

class Page1_1_1 : AppCompatActivity() {
    private lateinit var mobile: String
    private lateinit var btnNext: Button
    private lateinit var otp: EditText
    private lateinit var createPassword: EditText
    private lateinit var confirmPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page111)

        mobile = intent.getStringExtra("phone").toString()

        btnNext = findViewById(R.id.login_button)

        btnNext.setOnClickListener {
            otp = findViewById(R.id.otp)
            createPassword = findViewById(R.id.newPassword)
            confirmPassword = findViewById(R.id.confirmPassword)

            val queue = Volley.newRequestQueue(this@Page1_1_1)
            val url = "http://13.235.250.119/v2/reset_password/fetch_result"

            if (createPassword.text.toString() == confirmPassword.text.toString()) {

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobile)
                jsonParams.put("password", createPassword.text.toString())
                jsonParams.put("otp", otp.text.toString())

                print(jsonParams)
                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams,
                        Response.Listener {
                            println("Response is $it")
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")

                                if (success) {

                                    Toast.makeText(
                                        this@Page1_1_1, "Password has successfully changed",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    val intent = Intent(this@Page1_1_1, Page1::class.java)

                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this@Page1_1_1, "Enter Valid OTP",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }

                            } catch (e: Exception) {
                                e.printStackTrace()
                                Toast.makeText(
                                    this@Page1_1_1,
                                    "Unexpected error occurred",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this@Page1_1_1,
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


            } else {
                Toast.makeText(
                    this@Page1_1_1,
                    "Please enter same password in both fields",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}