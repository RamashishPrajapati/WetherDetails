package com.ram.weatherdetails.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ram.weatherdetails.R
import com.ram.weatherdetails.model.ServerResponse
import com.ram.weatherdetails.repository.WeatherDetailsRepository
import com.ram.weatherdetails.utils.NetworkHelper
import com.ram.weatherdetails.utils.NetworkState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherDetailsViewModel(application: Application) : AndroidViewModel(application) {

    private var weatherDetailsRepository: WeatherDetailsRepository = WeatherDetailsRepository()
    var weatherDetailsLiveData = MutableLiveData<NetworkState<ServerResponse>>()

    //to get the weather details as per user input
    fun getWeatherDetails(query: String, unit: String) {
        if (query.isNotEmpty()) {
            if (NetworkHelper.isNetworkConnected(getApplication())) {
                weatherDetailsLiveData.value = NetworkState.Loading()
                viewModelScope.launch(Dispatchers.IO) {
                    when (val response = weatherDetailsRepository.getWeatherDetails(query, unit)) {
                        is NetworkState.Error -> {
                            //Updating the error message with common message
                            weatherDetailsLiveData.postValue(
                                NetworkState.Error(
                                    getApplication<Application>().getString(
                                        R.string.data_not_available
                                    )
                                )
                            )
                        }

                        is NetworkState.Loading -> {
                        }

                        is NetworkState.Success -> {
                            weatherDetailsLiveData.postValue(response)

                        }
                    }
                }
            } else {
                weatherDetailsLiveData.value =
                    NetworkState.Error(getApplication<Application>().getString(R.string.check_internet_connection))
            }
        } else {
            weatherDetailsLiveData.value =
                NetworkState.Error(getApplication<Application>().getString(R.string.enter_input))
        }
    }

    //getting weather details as per user current latitude and longitude
    fun getWeatherDetailsByLatLon(lat: String, lon: String, unit: String) {
        if (lat == "0.0" && lon == "0.0") {
            weatherDetailsLiveData.value =
                NetworkState.Error(getApplication<Application>().getString(R.string.location_not_found))
        } else {
            if (NetworkHelper.isNetworkConnected(getApplication())) {
                weatherDetailsLiveData.value = NetworkState.Loading()
                viewModelScope.launch(Dispatchers.IO) {
                    when (val response =
                        weatherDetailsRepository.getWeatherDetailsByLatLon(lat, lon, unit)) {
                        is NetworkState.Error -> {
                            weatherDetailsLiveData.postValue(
                                NetworkState.Error(
                                    getApplication<Application>().getString(
                                        R.string.no_data_for_current_location
                                    )
                                )
                            )
                        }

                        is NetworkState.Loading -> {
                        }

                        is NetworkState.Success -> {
                            weatherDetailsLiveData.postValue(response)
                        }
                    }
                }
            } else {
                weatherDetailsLiveData.value =
                    NetworkState.Error(getApplication<Application>().getString(R.string.check_internet_connection))
            }
        }
    }

}