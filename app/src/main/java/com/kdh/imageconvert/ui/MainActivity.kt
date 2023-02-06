package com.kdh.imageconvert.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kdh.imageconvert.R
import com.kdh.imageconvert.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var  binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavigationView = binding.bottomNav
        bottomNavigationView.itemIconTintList = null
        val navController = supportFragmentManager.findFragmentById(R.id.fragment_container_view)?.findNavController()
        navController?.let{
            bottomNavigationView.setupWithNavController(navController)
        }

    }
}