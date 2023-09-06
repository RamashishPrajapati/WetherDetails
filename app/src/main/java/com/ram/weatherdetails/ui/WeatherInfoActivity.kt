package com.ram.weatherdetails.ui

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.ram.weatherdetails.R
import com.ram.weatherdetails.databinding.ActivityWetherInfoBinding
import com.ram.weatherdetails.utils.Constants
import com.ram.weatherdetails.utils.Constants.LOCATION_PERMISSION_REQUEST_CODE
import com.ram.weatherdetails.utils.NetworkState
import com.ram.weatherdetails.utils.PermissionUtils
import com.ram.weatherdetails.viewmodel.WeatherDetailsViewModel
import java.math.RoundingMode

class WeatherInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWetherInfoBinding
    private val weatherDetailsViewModel by lazy {
        ViewModelProvider(this)[WeatherDetailsViewModel::class.java]
    }

    private var unit = Constants.METRIC
    private var tempUnit = Constants.CELSIUS


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWetherInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iniListener()
        observeWeatherData()

    }

    override fun onStart() {
        super.onStart()
        checkGpsLocationAndPermission()
    }

    private fun iniListener() {
        binding.cvWeatherDetails.visibility = View.GONE

        binding.btnSubmit.setOnClickListener {
            weatherDetailsViewModel.getWeatherDetails(binding.etQuery.text.toString().trim(), unit)
        }

        binding.swTempUnit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                unit = Constants.IMPERIAL
                tempUnit = Constants.FAHRENHEIT
                updateTempUnit()
            } else {
                unit = Constants.METRIC
                tempUnit = Constants.CELSIUS
                updateTempUnit()
            }
        }
    }

    //Updating the temperature unit as per selection
    private fun updateTempUnit() {
        if (binding.tvCurrentTemp.text.toString().trim().isNotEmpty())
            binding.tvCurrentTemp.text =
                convertTemperature(
                    binding.tvCurrentTemp.text.toString().trim().dropLast(1).toDouble()
                ).toString() + tempUnit

        if (binding.tvMinTemperature.text.toString().trim().isNotEmpty())
            binding.tvMinTemperature.text =
                convertTemperature(
                    binding.tvMinTemperature.text.toString().trim().dropLast(1).toDouble()
                ).toString() + tempUnit

        if (binding.tvMaxTemperature.text.toString().trim().isNotEmpty())
            binding.tvMaxTemperature.text =
                convertTemperature(
                    binding.tvMaxTemperature.text.toString().trim().dropLast(1).toDouble()
                ).toString() + tempUnit

    }

    private fun observeWeatherData() {

        val progressDialog = progressDialog(getString(R.string.please_wait))

        weatherDetailsViewModel.weatherDetailsLiveData.observe(this) { weatherData ->
            weatherData.let {
                when (weatherData) {

                    is NetworkState.Loading -> {
                        progressDialog.show()
                    }

                    is NetworkState.Error -> {
                        progressDialog.dismiss()
                        showMessage(weatherData.error)
                    }

                    is NetworkState.Success -> {
                        progressDialog.dismiss()

                        binding.cvWeatherDetails.visibility = View.VISIBLE

                        Glide.with(this)
                            .load(
                                Constants.IMAGE_URL + weatherData.data.weather.first().icon + getString(
                                    R.string.png
                                )
                            )
                            .placeholder(android.R.drawable.ic_menu_camera)
                            .error(android.R.drawable.stat_notify_error).into(binding.ivWeather)

                        binding.tvCurrentTemp.text =
                            weatherData.data.main.temp.toString() + tempUnit
                        binding.tvWeatherCondition.text = weatherData.data.weather.first().main
                        binding.tvMinTemperature.text =
                            weatherData.data.main.temp_min.toString() + tempUnit
                        binding.tvMaxTemperature.text =
                            weatherData.data.main.temp_max.toString() + tempUnit
                        binding.tvHumidity.text =
                            weatherData.data.main.humidity.toString() + getString(
                                R.string.percentage
                            )
                        binding.tvWindSpeed.text =
                            weatherData.data.wind.speed.toString() + getString(
                                R.string.km_h
                            )
                    }
                }
            }
        }
    }

    private fun progressDialog(progressMessage: String): Dialog {
        return Dialog(this).apply {
            setContentView(LayoutInflater.from(context).inflate(R.layout.progress_dialogbox, null))
            setCancelable(false)
            var message = this.findViewById<TextView>(R.id.tvProgressMessage)
            message.text = progressMessage

        }
    }

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }

    private fun convertTemperature(temperature: Double): Double {

        return if (unit == Constants.IMPERIAL) {
            //convert celsius to fahrenheit
            (((temperature * 9) / 5) + 32).toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                .toDouble()

        } else {
            //convert fahrenheit to celsius
            (((temperature - 32) * 5) / 9).toBigDecimal().setScale(2, RoundingMode.HALF_UP)
                .toDouble()
        }
    }

    private fun checkGpsLocationAndPermission() {
        when {
            PermissionUtils.isAccessFineLocationGranted(this) -> {
                return when {
                    PermissionUtils.isLocationEnabled(this) -> {
                        setUpLocationListener()
                    }

                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(this)
                    }
                }
            }

            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    this, LOCATION_PERMISSION_REQUEST_CODE
                )
                return
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(this) -> {
                            setUpLocationListener()
                        }

                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(this)
                        }
                    }
                } else {
                    showMessage(getString(R.string.location_permission_not_granted))
                }


            }
        }
    }

    private fun setUpLocationListener() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            PermissionUtils.showGPSNotEnabledDialog(this)
            return
        }

        fusedLocationProviderClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener): CancellationToken {
                    return CancellationTokenSource().token
                }

                override fun isCancellationRequested(): Boolean {
                    return false
                }

            })
            .addOnSuccessListener { location ->
                if (location != null) {
                    weatherDetailsViewModel.getWeatherDetailsByLatLon(
                        location.latitude.toString(),
                        location.longitude.toString(),
                        unit
                    )
                }
            }
    }

}