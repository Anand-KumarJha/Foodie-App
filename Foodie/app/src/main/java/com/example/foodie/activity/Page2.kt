package com.example.foodie.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodie.*
import com.example.foodie.fragment.*
import com.google.android.material.navigation.NavigationView

class Page2 : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var frameLayout: FrameLayout
    private lateinit var navigationView: NavigationView
    private var previousMenuItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page2)

        sharedPreferences = getSharedPreferences("Foodie_preference", MODE_PRIVATE)

        drawerLayout = findViewById(R.id.drawerLayout)
        toolbar = findViewById(R.id.toolbar)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)

        setUpToolbar()
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@Page2, drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerLayout.closeDrawers()
                }
                R.id.profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, ProfileFragment()).commit()
                    supportActionBar?.title = "Profile"
                    drawerLayout.closeDrawers()
                }
                R.id.favourites -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FavouriteFragment()).commit()
                    supportActionBar?.title = "Favourites"
                    drawerLayout.closeDrawers()
                }
                R.id.orderHistory -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, OrderHistoryFragment()).commit()
                    supportActionBar?.title = "Order History"
                    drawerLayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, FAQFragment()).commit()
                    supportActionBar?.title = "FAQ"
                    drawerLayout.closeDrawers()
                }
                R.id.logout -> {
                    drawerLayout.closeDrawers()
                    val logout = AlertDialog.Builder(this)
                    logout.setTitle("Log Out")
                    logout.setMessage("Do you want to Log Out from Foodie app")
                    logout.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().clear().apply()
                        val intent = Intent(this@Page2, Page1::class.java)
                        finish()
                        startActivity(intent)
                    }
                    logout.setNegativeButton("No") { text, listener ->

                    }
                    logout.create()
                    logout.show()
                }

            }
            return@setNavigationItemSelectedListener true
        }

    }


    private fun openHome() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame, HomeFragment()).commit()
        supportActionBar?.title = "Home"
        navigationView.setCheckedItem(R.id.home)
        drawerLayout.closeDrawers()

    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Home"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        when (supportFragmentManager.findFragmentById(R.id.frame)) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }

    }
}