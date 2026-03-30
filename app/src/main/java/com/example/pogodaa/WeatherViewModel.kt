package com.example.pogodaa

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pogodaa.Utils.isTablet
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {
    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _forecastData = MutableLiveData<ForecastResponse>()
    val forecastData: LiveData<ForecastResponse> = _forecastData

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var context: Context

    private val refreshRunnable = object : Runnable {
        override fun run() {
            if (::context.isInitialized) {
                refreshWeatherData(context)
                handler.postDelayed(this, 10000)
                Log.e("Refresh","Refreshing")
            }
        }
    }

    fun initialize(context: Context) {
        this.context = context
        handler.post(refreshRunnable)
    }

    fun stopRefreshing() {
        handler.removeCallbacks(refreshRunnable)
    }

    fun refreshWeatherData(context: Context) {
        AppSettings.loadUnits(context)
        val currentLocation = AppSettings.currentLocation
        val currentUnits = AppSettings.units
        if (Utils.isOnline(context)) {
            loadWeatherData(currentLocation, currentUnits, context)
            loadForecastData(currentLocation, currentUnits, context)
            if (!isTablet(context)) {
                Toast.makeText(context, "Data refreshing...", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "No internet connection. Data may be outdated.", Toast.LENGTH_SHORT).show()
            loadDataFromStorage(context, "weather")
            loadDataFromStorage(context, "forecast")
        }
    }

    fun loadWeatherData(location: String, units: String, context: Context) {
        viewModelScope.launch {
            RetrofitClient.instance.getCurrentWeather(location, AppSettings.apiKey, units)
                .enqueue(object : Callback<WeatherResponse> {
                    override fun onResponse(call: Call<WeatherResponse>, response: Response<WeatherResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                _weatherData.postValue(it)
                                saveData(context, "weather", Gson().toJson(it))
                            }
                        } else {
                            Log.e("WeatherViewModel", "Error in response: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                        Log.e("WeatherViewModel", "Failure: ${t.message}")
                    }
                })
        }
    }

    fun loadForecastData(location: String, units: String, context: Context) {
        viewModelScope.launch {
            RetrofitClient.instance.getForecast(location, AppSettings.apiKey, units)
                .enqueue(object : Callback<ForecastResponse> {
                    override fun onResponse(call: Call<ForecastResponse>, response: Response<ForecastResponse>) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                _forecastData.postValue(it)
                                saveData(context, "forecast", Gson().toJson(it))
                            }
                        } else {
                            Log.e("WeatherViewModel", "Error in response: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                        Log.e("WeatherViewModel", "Failure: ${t.message}")
                    }
                })
        }
    }

    private fun saveData(context: Context, key: String, data: String) {
        val prefs = context.getSharedPreferences("weather_app", Context.MODE_PRIVATE)
        prefs.edit().putString(key, data).apply()
    }

    fun loadDataFromStorage(context: Context, key: String) {
        val prefs = context.getSharedPreferences("weather_app", Context.MODE_PRIVATE)
        val data = prefs.getString(key, null)
        data?.let {
            when (key) {
                "weather" -> _weatherData.postValue(Gson().fromJson(it, WeatherResponse::class.java))
                "forecast" -> _forecastData.postValue(Gson().fromJson(it, ForecastResponse::class.java))
            }
        }
    }
}
