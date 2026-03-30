package com.example.pogodaa

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.pogodaa.Utils.isTablet

class MainFragmentAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    private val pages = if (isTablet(fa)) {
        listOf(AllWeatherContainerFragment())
    } else {
        listOf(MenuFragment(), WeatherFragment(), DetailsFragment(), ForecastFragment())
    }

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = pages[position]
}


