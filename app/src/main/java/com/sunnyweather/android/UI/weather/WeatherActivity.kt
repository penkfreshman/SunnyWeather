package com.sunnyweather.android.UI.weather

import android.content.Context
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.sunnyweather.android.R
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*


class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy {  ViewModelProviders.of(this).get(WeatherViewModel::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        navBtn.setOnClickListener {
            drawableLayout.openDrawer(GravityCompat.START)
        }
        drawableLayout.addDrawerListener(object :DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {

            }

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {

            }

            override fun onDrawerClosed(drawerView: View) {
                val manager=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerOpened(drawerView: View) {

            }

        })

        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat=intent.getStringExtra("Location_lat")?:""
        }
        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng=intent.getStringExtra("Location_lng")?:""
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName=intent.getStringExtra("place_name")?:""
        }


        viewModel.weatherLiveData.observe(this, Observer {

            val weather=it.getOrNull()
            if (weather!=null){
                showWeatherInfo(weather)
            }else{

                Toast.makeText(this,"无法获取天气信息",Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
                swipeRefresh.isRefreshing=false
        })
        refeshWeather()
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        swipeRefresh.setOnRefreshListener {
            refeshWeather()
        }
    }
    private  fun showWeatherInfo(weather:Weather){
        placeName.text=viewModel.placeName
        val realtime=weather.realtime
        val daily=weather.daily
        val currentTempText="${realtime.temperature.toInt()}℃"
        currentTemp.text=currentTempText
        currentSky.text= getSky(realtime.skycon).info
        val currentPM25="空气指数${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text=currentPM25
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)

        forecastLayout.removeAllViews()
        val days=daily.skycon.size
        for (i in 0 until  days){


            val skycon=daily.skycon[i]
            val temperature=daily.temperature[i]

            val view=LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false)

            val dataInfo=view.findViewById<TextView>(R.id.dateInfo)
            val skyIcon=view.findViewById<ImageView>(R.id.skyIcon)
            val skyInfo=view.findViewById<TextView>(R.id.skyInfo)
            val temperatureInfo=view.findViewById<TextView>(R.id.temperatureInfo)

            val simpleDataFormat=SimpleDateFormat("yyy-MM-dd",Locale.getDefault())
            dataInfo.setText(simpleDataFormat.format(skycon.date))
            val sky= getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.setText(sky.info)
            val tempText="${temperature.min} ～${temperature.max} ℃"
            temperatureInfo.text=tempText

            forecastLayout.addView(view)
        }
        val lifeIndex=daily.lifeIndex
        coldRiskText.text=lifeIndex.coldRisk[0].desc
        dressingText.text=lifeIndex.dressing[0].desc
        ultravioletText.text=lifeIndex.ultraviolet[0].desc
        carWashingText.text=lifeIndex.carWashing[0].desc

        weatcherLayout.visibility= View.VISIBLE
    }
    fun refeshWeather(){
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        swipeRefresh.isRefreshing=true
    }
}
