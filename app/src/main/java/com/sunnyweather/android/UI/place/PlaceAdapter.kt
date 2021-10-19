package com.sunnyweather.android.UI.place

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sunnyweather.android.R
import com.sunnyweather.android.UI.weather.WeatherActivity

import com.sunnyweather.android.logic.model.Place
import kotlinx.android.synthetic.main.activity_weather.*


class PlaceAdapter(private val  fragment:PlaceFragment,private val placelist:List<Place>):RecyclerView.Adapter<PlaceAdapter.ViewHolder>(){
    inner class ViewHolder(view:View):RecyclerView.ViewHolder(view){
        val placeName:TextView=view.findViewById(R.id.place_name)
        val placeAddress:TextView=view.findViewById(R.id.place_address)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val view=LayoutInflater.from(parent.context).inflate(R.layout.place_item,parent,false)
        val  holder=ViewHolder(view)
        holder.itemView.setOnClickListener{
            val position=holder.adapterPosition
            val place=placelist[position]
            val actvity =fragment.activity
            if (actvity is WeatherActivity ){
                actvity.drawableLayout.closeDrawers()
               actvity.viewModel.placeName=place.name
                actvity.viewModel.locationLat=place.location.lat
                actvity.viewModel.locationLng=place.location.lng
                actvity.refeshWeather()
            }else{

                val intent= Intent(parent.context,WeatherActivity::class.java).apply {
                    putExtra("place_name",place.name)
                    putExtra("Location_lng",place.location.lng)
                    putExtra("Location_lat", place.location.lat)
                }
                fragment.viewModel.savePlace(place)
                fragment.startActivity(intent)
                fragment.activity?.finish()
            }
        }
        return  holder
    }

    override fun getItemCount(): Int =placelist.size
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place=placelist[position]
        holder.placeName.text=place.name
        holder.placeAddress.text=place.address
    }
}