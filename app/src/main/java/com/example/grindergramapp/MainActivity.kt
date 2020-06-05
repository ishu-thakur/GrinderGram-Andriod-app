package com.example.grindergramapp

import Fragments.HomeFragment
import Fragments.NotificationsFragment
import Fragments.ProfileFragment
import Fragments.SearchFragment
import android.content.Intent
import android.os.Bundle
import android.widget.AutoCompleteTextView
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {



    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.nav_home -> {
        moveToFrag(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_search -> {
            moveToFrag(SearchFragment())
            return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add_post -> {
                item.isChecked = false
                startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_notifications -> {
            moveToFrag(NotificationsFragment())
            return@OnNavigationItemSelectedListener true
            }
            R.id.nav_profile -> {
            moveToFrag(ProfileFragment())
            return@OnNavigationItemSelectedListener true
            }
        }
        //means if the fragments is clicked on then it will call that fragment
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
//        to start the page from home again when it is restarted
//        supportFragmentManager.beginTransaction().replace(R.id.fragment_container,HomeFragment()).commit()

        moveToFrag(HomeFragment())

    }
    //making a private method so whenever we click this mehtod call and pass home , serach as parameter and that page got open
    private fun moveToFrag(fragment: Fragment)
    {
        val fragTrans = supportFragmentManager.beginTransaction()
        fragTrans.replace(R.id.fragment_container,fragment)
        fragTrans.commit()


    }
}
