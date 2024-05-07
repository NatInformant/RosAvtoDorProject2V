package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RequestBody
import com.example.rosavtodorproject2.data.models.RequestPoint
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MapRemoteDataSource {

    private val RADIUS: Double = 100.0
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
            RADIUS
        )
        if (response.isSuccessful) {
            response.body()?.points?.forEach {
                points.add(it)
            }
        }

        return points
    }

    suspend fun addPoint(newPoint: MyPoint) {

        val response = mapPointsApi.addPoint(
            requestBody = RequestBody(
                RequestPoint(
                    newPoint.coordinates,
                    if(newPoint.name == "") { null } else {newPoint.name},
                    when (newPoint.type) {
                        5 -> "RoadAccident"
                        6 -> "RoadDisadvantages"
                        7 -> "Roadblock"
                        else -> "ThirdPartyIllegalActions"
                    }
                ),
                null
            )
        )

        if (response.isSuccessful) {
            //Хз надо ли вообще тут что-то проверять?
        }
    }
}