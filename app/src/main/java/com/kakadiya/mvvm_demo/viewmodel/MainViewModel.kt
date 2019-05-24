package com.kakadiya.mvvm_demo.viewmodel

import android.content.Context
import android.content.res.Resources
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import com.kakadiya.mvvm_demo.MVVMApplication
import com.kakadiya.mvvm_demo.R
import com.kakadiya.mvvm_demo.data.WeatherResponse
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Observable

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by krish on 2019-05-09.
 */

class MainViewModel(private var context: Context?) : Observable() {
    var progressbar: ObservableInt
    var mainLayout: ObservableInt
    var temperature: ObservableField<String>
    var maxTemperature: ObservableField<String>
    var minTemperature: ObservableField<String>
    var dateTime: ObservableField<String>
    var description: ObservableField<String>
    var image: ObservableField<Drawable>

    var weatherResponse: WeatherResponse? = null
        set(weatherResponse) {
            field = weatherResponse
            temperature.set(convertKelvinToCelcius(weatherResponse?.main!!.temp!!).toString())
            maxTemperature.set("Day " + convertKelvinToCelcius(weatherResponse.main!!.tempMax!!).toString())
            minTemperature.set("Night " + convertKelvinToCelcius(weatherResponse.main!!.tempMin!!).toString())
            description.set(weatherResponse.weather!![0].description.toString())
            val imageURL = "http://openweathermap.org/img/w/" + weatherResponse.weather!![0].icon + ".png"
            if (bindableFieldTarget != null) {
                Picasso.get()
                        .load(imageURL)
                        .placeholder(R.mipmap.weather_icon)
                        .into(bindableFieldTarget)
            }
        }
    private val bindableFieldTarget: BindableFieldTarget?

    internal var appId = "b6907d289e10d714a6e88b30761fae22"


    private var compositeDisposable: CompositeDisposable? = CompositeDisposable()

    val dateFormatted: String
        get() {
            val dateFormat = SimpleDateFormat("MMMM dd, HH:mm")
            val date = Date()
            return dateFormat.format(date)
        }

    init {
        progressbar = ObservableInt(View.GONE)
        mainLayout = ObservableInt(View.VISIBLE)
        temperature = ObservableField("Temp")
        maxTemperature = ObservableField("Day Temp")
        minTemperature = ObservableField("Night Temp")
        description = ObservableField("Description")
        dateTime = ObservableField("January 7, 14:55")
        image = ObservableField()
        bindableFieldTarget = BindableFieldTarget(image, context!!.getResources())
        //("http://openweathermap.org/img/w/04n.png")
    }

    private fun convertKelvinToCelcius(kelvin: Double): Int {
        return (kelvin - 273.15).toInt()
    }


    fun loadData(longitude: Double, latitude: Double) {
        initializeViews()
        fetchWeatherData(longitude, latitude)
    }

    private fun initializeViews() {
        progressbar.set(View.VISIBLE)
        mainLayout.set(View.GONE)
        dateTime.set(dateFormatted)
    }

    private fun fetchWeatherData(longitude: Double, latitude: Double) {

        val mMVVMApplication = MVVMApplication.create(context)
        val weatherService = mMVVMApplication.weatherService

        val disposable = weatherService?.fetchWeather(latitude, longitude, appId)?.subscribeOn(mMVVMApplication.subscribeScheduler())?.observeOn(AndroidSchedulers.mainThread())?.subscribe({
            Log.e("getHumidity", it.main!!.humidity.toString())
            progressbar.set(View.GONE)
            mainLayout.set(View.VISIBLE)
            weatherResponse = it
        }) {
            progressbar.set(View.GONE)
            mainLayout.set(View.VISIBLE)
            description.set(context!!.getString(R.string.error_loading_data))
        }
        compositeDisposable!!.add(disposable!!)
    }

    inner class BindableFieldTarget(private val observableField: ObservableField<Drawable>, private val resources: Resources) : Target {

        override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
            observableField.set(BitmapDrawable(resources, bitmap))
        }

        override fun onBitmapFailed(e: Exception, errorDrawable: Drawable) {
            observableField.set(errorDrawable)
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable) {
            observableField.set(placeHolderDrawable)
        }
    }

    fun reset() {
        unsubscribeFromDisposable()
        compositeDisposable = null
        context = null

    }

    private fun unsubscribeFromDisposable() {
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed) {
            compositeDisposable!!.dispose()
        }
    }
}
