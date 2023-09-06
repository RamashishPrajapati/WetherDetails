package com.ram.weatherdetails.retrofit


import com.ram.weatherdetails.model.ServerResponse
import com.ram.weatherdetails.utils.Constants
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface WeatherRequestApi {

    @POST(Constants.WEATHER_DETAILS)
    suspend fun getWeatherDetails(
        @Query("q") query: String,
        @Query("units") units: String
    ): Response<ServerResponse>

    @POST(Constants.WEATHER_DETAILS)
    suspend fun getWeatherDetailsByLatLon(
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("units") units: String
    ): Response<ServerResponse>
}