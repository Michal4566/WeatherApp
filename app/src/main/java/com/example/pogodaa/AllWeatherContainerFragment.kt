package com.example.pogodaa

import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class AllWeatherContainerFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layoutResId = if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            R.layout.fragment_all_weather_container_portrait
        } else {
            R.layout.fragment_all_weather_container
        }
        return inflater.inflate(layoutResId, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction().apply {
            replace(R.id.menuContainer, MenuFragment())
            replace(R.id.weatherContainer, WeatherFragment())
            replace(R.id.detailsContainer, DetailsFragment())
            replace(R.id.forecastContainer, ForecastFragment())
            commit()
        }
    }
}
