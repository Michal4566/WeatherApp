package com.example.pogodaa

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager2
    private lateinit var viewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]

        AppSettings.loadUnits(this)
        AppSettings.loadFavoriteLocations(this)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = MainFragmentAdapter(this)

        if (Utils.isOnline(this)) {
            viewModel.loadWeatherData(AppSettings.currentLocation, AppSettings.units, this)
            viewModel.loadForecastData(AppSettings.currentLocation, AppSettings.units, this)
        } else {
            Toast.makeText(this, "No internet connection. Loading cached data.", Toast.LENGTH_SHORT).show()
            viewModel.loadDataFromStorage(this, "weather")
            viewModel.loadDataFromStorage(this, "forecast")
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.initialize(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.stopRefreshing()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.stopRefreshing()
        AppSettings.saveFavoriteLocations(this)
    }
}


