package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RequestPointBody
import com.example.rosavtodorproject2.data.models.RequestPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapRemoteDataSource {

    private val mapPointsApi: MapPointsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapPointsApi::class.java)
    }

    private val points: MutableList<MyPoint> = mutableListOf()

    fun loadPoints() = points
    suspend fun getPoints(currentLatitude: Double, currentLongitude: Double): List<MyPoint> {
        points.clear()
        val response = mapPointsApi.getPoints(
            currentLatitude,
            currentLongitude,
        )
        if (response.isSuccessful) {
            response.body()?.points?.forEach {
                points.add(it)
            }
        }
        //toList()?
        return points
    }

    suspend fun addPoint(newPoint: MyPoint, reliability:Int) {

        val response = mapPointsApi.addPoint(
            requestPointBody = RequestPointBody(
                RequestPoint(
                    type = newPoint.type -5,
                    coordinates = newPoint.coordinates,
                    description = if(newPoint.name == "") { null } else {newPoint.name},
                    /*reliability = reliability*/
                )
            )
        )

        if (response.isSuccessful) {
            //Хз надо ли вообще тут что-то проверять?
        }
    }
}