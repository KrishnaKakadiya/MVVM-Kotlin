package com.kakadiya.mvvm_demo.data

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by krish on 2019-05-09.
 */

interface WeatherService {

    @GET("data/2.5/weather")
    fun fetchWeather(@Query("lat") lat: Double, @Query("lon") lon: Double, @Query("appid") appID: String): Observable<WeatherResponse>
}
