package com.example.pogodaa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class WeatherFragment : Fragment() {
    private val viewModel: WeatherViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        val tvLocation: TextView = view.findViewById(R.id.tvLocation)
        val tvCoordinates: TextView = view.findViewById(R.id.tvCoordinates)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvTemperature: TextView = view.findViewById(R.id.tvTemperature)
        val tvFeelsLike: TextView = view.findViewById(R.id.tvFeelsLike)
        val tvTempRange: TextView = view.findViewById(R.id.tvTempRange)
        val tvPressure: TextView = view.findViewById(R.id.tvPressure)
        val tvDescription: TextView = view.findViewById(R.id.tvDescription)
        val ivWeatherIcon: ImageView = view.findViewById(R.id.ivWeatherIcon)

        viewModel.weatherData.observe(viewLifecycleOwner) { weather ->
            tvLocation.text = weather.name
            tvCoordinates.text = "Lat: ${weather.coord.lat}, Lon: ${weather.coord.lon}"
            tvTime.text = SimpleDateFormat("HH:mm, dd MMM yyyy", Locale.getDefault()).format(Date(weather.dt * 1000))

            val unit = if (AppSettings.units == "metric") "°C" else "°F"
            tvTemperature.text = "${weather.main.temp}$unit"
            tvFeelsLike.text = "Odczuwalna: ${weather.main.feels_like}$unit"
            tvTempRange.text = "↓${weather.main.temp_min}$unit  ↑${weather.main.temp_max}$unit"

            tvPressure.text = "${weather.main.pressure} hPa"
            tvDescription.text = weather.weather.first().description.capitalize(Locale.getDefault())
            Picasso.get().load("https://openweathermap.org/img/wn/${weather.weather.first().icon}@2x.png").into(ivWeatherIcon)
        }

        if (viewModel.weatherData.value == null) {
            viewModel.refreshWeatherData(requireContext())
        }

        return view
    }
}
