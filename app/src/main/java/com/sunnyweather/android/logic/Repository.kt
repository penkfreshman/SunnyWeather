package com.sunnyweather.android.logic

import android.util.Log
import android.view.Display
import androidx.lifecycle.liveData
import com.sunnyweather.android.SunnyWeatherApp
import com.sunnyweather.android.logic.Dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.PlaceResponse
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeathernetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.invoke
import okhttp3.Dispatcher
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

object Repository {


    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeathernetwork.searchPlace(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }
    fun refreshWeather(lng:String,lat:String,placeName:String)= fire(Dispatchers.IO) {

        coroutineScope {
            val deferredRealtime =async {
                SunnyWeathernetwork.getRealtimeWeather(lng,lat)

            }
            val  derredDailyWeather=async {
                SunnyWeathernetwork.getDailyWeather(lng,lat)
            }

            val realtimeResponse=deferredRealtime.await()
            val  dailyResponse=derredDailyWeather.await()
            if (realtimeResponse.status=="ok"&&dailyResponse.status=="ok"){

                val weather=Weather(realtimeResponse.result.realtime,dailyResponse.result.daily)
                Result.success(weather)
            }else{
                Result.failure(
                    RuntimeException("realtime response status is ${realtimeResponse.status}"
                    +"Daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }


    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }

    fun savePlace(place:Place)=PlaceDao.savePlace(place)
    fun isPlacesave()=PlaceDao.isPlaceSaved()
    fun  getPlace()=PlaceDao.getSavedPlace()
}