package com.example.hazir.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.hazir.R
import com.example.hazir.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
     lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupBottomNavigationView()

    }

    private fun setupBottomNavigationView() {
        val colorStateList = ContextCompat.getColorStateList(this, R.color.se)
        binding.bottomNavigationView.itemIconTintList = colorStateList
        val navController = findNavController(R.id.fragmentContainerView)
        binding.bottomNavigationView.setupWithNavController(navController)
        binding.bottomNavigationView.itemActiveIndicatorColor = getColorStateList(R.color.white)
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            if (navController.currentDestination?.id != menuItem.itemId) {
                navController.navigate(menuItem.itemId)
            }
            true
        }
    }
}