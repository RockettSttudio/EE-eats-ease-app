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
        val isTablet = resources.getBoolean(R.bool.isTablet)
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.nav_view)


        if (isTablet) {
            val navController2 = findNavController(R.id.nav_host_fragment_activity_main2)
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
                val navController2 = findNavController(R.id.nav_host_fragment_activity_main2)
                setTopNavigationVisibility(View.GONE)
                setBottomNavigationVisibility(View.GONE)
                navController2.navigate(R.id.navigation_Settings)
                navController.navigate(R.id.blankFragment)

            }else{
                setTopNavigationVisibility(View.GONE)
                setBottomNavigationVisibility(View.GONE)
                navController.navigate(R.id.navigation_Settings)
            }
        }

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        when (navController.currentDestination?.id) {
            R.id.navigation_home, R.id.navigation_recipes, R.id.navigation_saved -> {
                setTopNavigationVisibility(View.VISIBLE)
                setBottomNavigationVisibility(View.VISIBLE)
                navController.navigateUp()
            }
            R.id.navigation_Settings -> {
                setTopNavigationVisibility(View.VISIBLE)
                setBottomNavigationVisibility(View.VISIBLE)
                navController.navigateUp()
            }
            R.id.recipeDetailsFragment -> {
                setTopNavigationVisibility(View.VISIBLE)
                setBottomNavigationVisibility(View.VISIBLE)
                navController.navigateUp()
            }
            else -> {
                super.onBackPressedDispatcher.onBackPressed()
            }
        }
    }


    fun setTopNavigationVisibility(visibility: Int) {
        val topNavigationView: NavigationView = findViewById(R.id.top_navigation)
        topNavigationView.visibility = visibility
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.nav_view)
        bottomNavigationView.visibility = visibility
    }


}