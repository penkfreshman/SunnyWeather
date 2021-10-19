package com.sunnyweather.android.UI.place

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer

import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.sunnyweather.android.MainActivity
import com.sunnyweather.android.R
import com.sunnyweather.android.UI.weather.WeatherActivity
import kotlinx.android.synthetic.main.fragment_place.*


class PlaceFragment: Fragment(){

    val viewModel by lazy { ViewModelProviders.of(this).get(PlaceViewModel::class.java) }
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place,container,false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (viewModel.issavePlace()&&activity is MainActivity){
            val place=viewModel.getSavePlace()
            val intent= Intent(activity, WeatherActivity::class.java).apply {
                putExtra("place_name",place.name)
                putExtra("Location_lng",place.location.lng)
                putExtra("Location_lat", place.location.lat)
            }
            startActivity(intent)
            activity?.finish()
            return
        }
        val layoutManager=LinearLayoutManager(activity)
        recycleView.layoutManager=layoutManager
        adapter= PlaceAdapter(this,viewModel.placeList)
        recycleView.adapter=adapter
        search_place_edt.addTextChangedListener(object :TextWatcher{

            override fun afterTextChanged(s: Editable?) {
              val content=s.toString()
                if(content.isNotEmpty()){

                    viewModel.searchPlace(content)
                }else{

                    recycleView.visibility=View.VISIBLE
                    bgImageview.visibility=View.VISIBLE
                    viewModel.placeList.clear()
                    adapter.notifyDataSetChanged()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }


        })
        viewModel.PlaceliveData.observe(this, Observer {
            val place=it.getOrNull()
            if (place!=null){
                recycleView.visibility=View.VISIBLE
                bgImageview.visibility=View.GONE
                viewModel.placeList.clear()
                viewModel.placeList.addAll(place)
                adapter.notifyDataSetChanged()
            }else{
                Toast.makeText(activity,"未能查询到任何地址",Toast.LENGTH_SHORT).show()
                it.exceptionOrNull()?.printStackTrace()
            }
        })
    }
}