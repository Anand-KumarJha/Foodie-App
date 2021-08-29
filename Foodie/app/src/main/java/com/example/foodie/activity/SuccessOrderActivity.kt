package com.example.foodie.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.foodie.R
import com.example.foodie.activity.Page2

class SuccessOrderActivity : AppCompatActivity() {
//    lateinit var ok: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_order)

//        ok = findViewById(R.id.OK)
//        ok.setOnClickListener{
//            onBackPressed()
//        }

        Handler().postDelayed({
            finish()
            onBackPressed()
        }, 3000)
    }

    override fun onBackPressed() {
        //Disabled
    }
}