package com.example.pogodaa

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("data/2.5/weather")
    fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String): Call<WeatherResponse>

    @GET("data/2.5/forecast")
    fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String): Call<ForecastResponse>

    @GET("geo/1.0/direct")
    fun getLocations(
        @Query("q") cityName: String,
        @Query("limit") limit: Int = 5,
        @Query("appid") apiKey: String
    ): Call<List<GeocodingResponse>>
}
