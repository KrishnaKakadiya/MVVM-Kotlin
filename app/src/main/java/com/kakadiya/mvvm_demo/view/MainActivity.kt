package com.kakadiya.mvvm_demo.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.kakadiya.mvvm_demo.R
import com.kakadiya.mvvm_demo.databinding.ActivityMainBinding
import com.kakadiya.mvvm_demo.viewmodel.MainViewModel

import java.util.Observable
import java.util.Observer

class MainActivity : AppCompatActivity(), Observer {

    private var binding: ActivityMainBinding? = null
    private var mainViewModel: MainViewModel? = null

    val currentLocation: Location?
        get() {
            val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }
            assert(lm != null)
            val location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10f, locationListener)

            return location
        }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            if (location != null) {
                LoadData(location)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

        override fun onProviderEnabled(provider: String) {}

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(applicationContext, "No location found", Toast.LENGTH_LONG)
        }
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        setupObserver(mainViewModel)
        val location = currentLocation
        if (location != null) {
            LoadData(location)
        }
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        mainViewModel = MainViewModel(applicationContext)
        binding!!.setMainViewModel(mainViewModel)
    }

    private fun LoadData(location: Location?) {
        mainViewModel!!.loadData(location!!.longitude, location.latitude)
    }

    override fun update(observable: Observable, arg: Any) {
        if (observable is MainViewModel) {
            val mainViewModel = observable
        }
    }

    private fun setupObserver(observable: Observable?) {
        observable!!.addObserver(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel!!.reset()
    }
}
