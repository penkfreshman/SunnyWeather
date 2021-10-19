package com.sunnyweather.android.UI.place

import android.util.Log
import android.view.animation.Transformation
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

class PlaceViewModel :ViewModel(){
    private  val searchLiveData= MutableLiveData<String>()

    val placeList=ArrayList<Place>()

    val PlaceliveData= Transformations.switchMap(searchLiveData){
        it->Repository.searchPlaces(it)

    }
   fun searchPlace(query:String){
       searchLiveData.value=query
   }
    fun savePlace(place: Place)=Repository.savePlace(place)
    fun issavePlace()=Repository.isPlacesave()
    fun getSavePlace()=Repository.getPlace()

}