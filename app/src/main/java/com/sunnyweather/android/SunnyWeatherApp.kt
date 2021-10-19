package com.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunnyWeatherApp :Application(){
    companion object{
        const val TAKEN="CxWRm5B1roUF4M0S"

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context



    }

    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }

}