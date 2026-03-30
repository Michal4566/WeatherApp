package com.example.pogodaa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import java.text.SimpleDateFormat
import java.util.*

class DetailsFragment : Fragment() {
    private val viewModel: WeatherViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details, container, false)
        val tvLocation: TextView = view.findViewById(R.id.tvCity)
        val tvWind: TextView     = view.findViewById(R.id.tvWind)
        val tvHumidity: TextView = view.findViewById(R.id.tvHumidity)
        val tvVisibility: TextView = view.findViewById(R.id.tvVisibility)
        val tvClouds: TextView   = view.findViewById(R.id.tvClouds)
        val tvSunrise: TextView  = view.findViewById(R.id.tvSunrise)
        val tvSunset: TextView   = view.findViewById(R.id.tvSunset)

        val timeFmt = SimpleDateFormat("HH:mm", Locale.getDefault())

        viewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            tvLocation.text   = weather.name
            tvWind.text       = "Wind: ${weather.wind.speed} m/s, ${weather.wind.deg}°"
            tvHumidity.text   = "Humidity: ${weather.main.humidity}%"
            tvVisibility.text = "Visibility: ${weather.visibility} meters"
            tvClouds.text     = "${weather.clouds.all}%"
            tvSunrise.text    = timeFmt.format(Date(weather.sys.sunrise * 1000))
            tvSunset.text     = timeFmt.format(Date(weather.sys.sunset  * 1000))
        }

        if (viewModel.weatherData.value == null) {
            viewModel.refreshWeatherData(requireContext())
        }

        return view
    }
}
