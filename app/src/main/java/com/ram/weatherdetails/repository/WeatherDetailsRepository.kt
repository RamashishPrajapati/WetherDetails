package com.ram.weatherdetails.repository


import com.ram.weatherdetails.model.ServerResponse
import com.ram.weatherdetails.retrofit.RetrofitInstance
import com.ram.weatherdetails.utils.NetworkState
import org.json.JSONObject

class WeatherDetailsRepository {

    private val retrofit = RetrofitInstance.weatherApi

    suspend fun getWeatherDetails(query: String, unit: String): NetworkState<ServerResponse> {
        val response = retrofit.getWeatherDetails(query, unit)

        return if (response.isSuccessful) {
            NetworkState.Success(response.body()!!)
        } else {
            val jsonObject = JSONObject(response?.errorBody()?.string())
            var message = jsonObject.getString("message")
            NetworkState.Error(message)
        }
    }

    suspend fun getWeatherDetailsByLatLon(
        lat: String,
        lon: String,
        unit: String
    ): NetworkState<ServerResponse> {
        val response = retrofit.getWeatherDetailsByLatLon(lat, lon, unit)

        return if (response.isSuccessful) {
            NetworkState.Success(response.body()!!)
        } else {
            val jsonObject = JSONObject(response?.errorBody()?.string())
            var message = jsonObject.getString("message")
            NetworkState.Error(message)
        }
    }
}