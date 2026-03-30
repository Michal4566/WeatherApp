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
import kotlin.math.roundToInt

class ForecastFragment : Fragment() {
    private val viewModel: WeatherViewModel by viewModels({ requireActivity() })

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forecast, container, false)
        val tvCityName: TextView      = view.findViewById(R.id.tvCityName)
        val ivDay1: ImageView         = view.findViewById(R.id.ivWeatherIconDay1)
        val tvDay1: TextView          = view.findViewById(R.id.tvDay1)
        val ivDay2: ImageView         = view.findViewById(R.id.ivWeatherIconDay2)
        val tvDay2: TextView          = view.findViewById(R.id.tvDay2)
        val tvPopDay2: TextView       = view.findViewById(R.id.tvPopDay2)
        val ivDay3: ImageView         = view.findViewById(R.id.ivWeatherIconDay3)
        val tvDay3: TextView          = view.findViewById(R.id.tvDay3)
        val tvPopDay3: TextView       = view.findViewById(R.id.tvPopDay3)
        val ivDay4: ImageView         = view.findViewById(R.id.ivWeatherIconDay4)
        val tvDay4: TextView          = view.findViewById(R.id.tvDay4)
        val tvPopDay4: TextView       = view.findViewById(R.id.tvPopDay4)
        val ivDay5: ImageView         = view.findViewById(R.id.ivWeatherIconDay5)
        val tvDay5: TextView          = view.findViewById(R.id.tvDay5)
        val tvPopDay5: TextView       = view.findViewById(R.id.tvPopDay5)

        fun loadIcon(iv: ImageView, icon: String) =
            Picasso.get().load("https://openweathermap.org/img/wn/${icon}@2x.png").into(iv)

        fun popText(pop: Double) = "💧 ${(pop * 100).roundToInt()}%"

        viewModel.forecastData.observe(viewLifecycleOwner) { forecast ->
            tvCityName.text = forecast.city.name
            val days = forecast.list
            val unit = if (AppSettings.units == "metric") "°C" else "°F"

            loadIcon(ivDay1, days[0].weather[0].icon)
            loadIcon(ivDay2, days[8].weather[0].icon)
            loadIcon(ivDay3, days[16].weather[0].icon)
            loadIcon(ivDay4, days[24].weather[0].icon)
            loadIcon(ivDay5, days[32].weather[0].icon)

            tvDay1.text = "Day 1: ${days[0].dt_txt} \n Temp: ${days[0].main.temp}$unit"
            tvDay2.text = "Day 2: ${days[8].dt_txt} \n Temp: ${days[8].main.temp}$unit"
            tvDay3.text = "Day 3: ${days[16].dt_txt} \n Temp: ${days[16].main.temp}$unit"
            tvDay4.text = "Day 4: ${days[24].dt_txt} \n Temp: ${days[24].main.temp}$unit"
            tvDay5.text = "Day 5: ${days[32].dt_txt} \n Temp: ${days[32].main.temp}$unit"

            tvPopDay2.text = popText(days[8].pop)
            tvPopDay3.text = popText(days[16].pop)
            tvPopDay4.text = popText(days[24].pop)
            tvPopDay5.text = popText(days[32].pop)
        }

        if (viewModel.forecastData.value == null) {
            viewModel.refreshWeatherData(requireContext())
        }

        return view
    }
}
