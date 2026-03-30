package com.example.pogodaa

import android.content.Context
import java.io.File

object AppSettings {
    var currentLocation: String = "Warsaw, Masovian Voivodeship, PL"
    var units: String = "metric"
    var favoriteLocations: MutableSet<String> = mutableSetOf()

    const val apiKey: String = ""

    fun loadFavoriteLocations(context: Context) {
        val file = File(context.filesDir, "favorite_locations.txt")
        if (file.exists()) {
            favoriteLocations = file.readLines().toMutableSet()
        } else {
            favoriteLocations = mutableSetOf("Warsaw, Masovian Voivodeship, PL")
            saveFavoriteLocations(context)
        }
    }

    fun saveFavoriteLocations(context: Context) {
        val file = File(context.filesDir, "favorite_locations.txt")
        file.writeText(favoriteLocations.joinToString("\n"))
    }

    fun loadUnits(context: Context) {
        val file = File(context.filesDir, "units.txt")
        if (file.exists()) {
            units = file.readText()
        } else {
            units = "metric"
            saveUnits(context)
        }
    }

    fun saveUnits(context: Context) {
        val file = File(context.filesDir, "units.txt")
        file.writeText(units)
    }

}
