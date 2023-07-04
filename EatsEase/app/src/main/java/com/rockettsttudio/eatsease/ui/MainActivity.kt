package com.rockettsttudio.eatsease.ui

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rockettsttudio.eatsease.R
import com.rockettsttudio.eatsease.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navController2 = findNavController(R.id.nav_host_fragment_activity_main2)
        val isTablet = resources.getBoolean(R.bool.isTablet)

        if (isTablet) {
            navController2.navigate(R.id.blankFragment)
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_saved, R.id.navigation_recipes
            )
        )
        binding.settingsImageButton.setOnClickListener {
            if (isTablet) {
                setTopNavigationVisibility(View.GONE)
                navController2.navigate(R.id.navigation_Settings)
                navController.navigate(R.id.blankFragment)

            }else{
                setTopNavigationVisibility(View.GONE)
                navController.navigate(R.id.navigation_Settings)
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()
    }

    fun setTopNavigationVisibility(visibility: Int) {
        val topNavigationView: NavigationView = findViewById(R.id.top_navigation)
        topNavigationView.visibility = visibility
    }

}