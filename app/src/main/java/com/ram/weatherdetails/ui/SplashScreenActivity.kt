package com.ram.weatherdetails.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.ram.weatherdetails.databinding.ActivitySplashScreenBinding
import com.ram.weatherdetails.viewmodel.SplashViewModel

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var splashViewModel: SplashViewModel
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()
        observerSplashData()

    }

    private fun initViewModel() {
        splashViewModel = ViewModelProvider(this)[SplashViewModel::class.java]

    }

    private fun observerSplashData() {
        splashViewModel.initSplashScreen()
        splashViewModel.splashLiveData.observe(this, Observer {
            finish()
            startActivity(Intent(this, WeatherInfoActivity::class.java))
        })
    }
}