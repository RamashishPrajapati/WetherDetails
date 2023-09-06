package com.ram.weatherdetails.retrofit

import com.ram.weatherdetails.utils.Constants
import com.ram.weatherdetails.utils.Keys
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    /*Adding the api key to every request */
    private fun apiKeyAsQuery(chain: Interceptor.Chain) = chain.proceed(
        chain.request()
            .newBuilder()
            .url(
                chain.request().url.newBuilder().addQueryParameter("appid", Keys.apiKey())
                    .build()
            )
            .build()
    )


    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { apiKeyAsQuery(it) }
        .build()

    val weatherApi: WeatherRequestApi by lazy {
        Retrofit.Builder()
            .client(client)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherRequestApi::class.java)
    }
}
