package com.ram.weatherdetails.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : ViewModel() {

    var splashLiveData: MutableLiveData<String> = MutableLiveData()

    fun initSplashScreen() {
        viewModelScope.launch {
            delay(2000)
            updateSplashScreen()
        }
    }

    private fun updateSplashScreen() {
        splashLiveData.value = "SplashScreen"
    }

}