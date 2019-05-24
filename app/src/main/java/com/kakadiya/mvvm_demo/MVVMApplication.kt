package com.kakadiya.mvvm_demo

import android.app.Application
import android.content.Context

import com.kakadiya.mvvm_demo.data.WeatherFactory
import com.kakadiya.mvvm_demo.data.WeatherService

import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

/**
 * Created by krish on 2019-05-09.
 */

class MVVMApplication : Application() {

    var weatherService: WeatherService? = null
        get() {
            if (field == null) {
                this.weatherService = WeatherFactory.create()
            }
            return field
        }
    private var scheduler: Scheduler? = null
    fun subscribeScheduler(): Scheduler {
        if (scheduler == null) {
            scheduler = Schedulers.io()
        }

        return scheduler as Scheduler
    }

    fun setScheduler(scheduler: Scheduler) {
        this.scheduler = scheduler
    }

    companion object {

        private operator fun get(context: Context?): MVVMApplication {
            return context!!.applicationContext as MVVMApplication
        }

        fun create(context: Context?): MVVMApplication {
            return MVVMApplication[context]
        }
    }
}
