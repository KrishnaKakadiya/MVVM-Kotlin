package com.kakadiya.mvvm_demo

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager

import com.kakadiya.mvvm_demo.view.MainActivity
import com.kakadiya.mvvm_demo.viewmodel.MainViewModel

import org.junit.Test
import org.mockito.Mock
import org.junit.Assert.*

/**
 * Created by krish on 2019-05-10.
 */

class MockLocationProvider(internal var providerName: String, internal var ctx: Context) {
    internal var lm: LocationManager
    internal var location: Location

    @Mock
    internal var locationListener: LocationListener? = null

    @Mock
    internal var mainViewModel: MainViewModel? = null

    @Mock
    internal var mainActivity: MainActivity? = null

    init {

        lm = ctx.getSystemService(
                Context.LOCATION_SERVICE) as LocationManager
        lm.addTestProvider(providerName, false, false, false, false, false,
                true, true, 0, 5)
        lm.setTestProviderEnabled(providerName, true)
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
    }

    @Test
    fun testCurrentLocation() {
        assertEquals(location, mainActivity!!.currentLocation)
        assertEquals(null, mainActivity!!.currentLocation)
    }
}
