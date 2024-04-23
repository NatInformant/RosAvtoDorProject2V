package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RequestBody
import com.example.rosavtodorproject2.data.models.RequestPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class MapRemoteDataSource {

    private val BASE_URL = "https://smiling-striking-lionfish.ngrok-free.app/api/"
    private val mapPointsApi: MapPointsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapPointsApi::class.java)
    }

    private val points: MutableList<MyPoint> = mutableListOf(
        MyPoint(
            Coordinates(54.944265, 60.819966),
            "Гостинница: Ника",
            3,
        ),
        MyPoint(
            Coordinates(54.944153, 60.818947),
            "Азс: Экотоп",
            0,
        ),
        MyPoint(
            Coordinates(54.916774, 60.353165),
            "Кафе: Крепость М5",
            1,
        ),
    )

    fun loadPoints() = points
    suspend fun getPoints(currentLatitude: Double, currentLongitude: Double): List<MyPoint> {

        //Ниже делается запрос к апишке, пока закомментил, но потом надо будет вернуть как было.
        /*val response = mapPointsApi.getPoints(
            currentLatitude,
            currentLongitude
        )
        if (response.isSuccessful) {
            response.body()?.points?.forEach { points.add(it) }
        }*/

        return points
    }

    suspend fun addPoint(newPoint: MyPoint) {

        //Ниже делается запрос к апишке, пока закомментил, но потом надо будет вернуть как было.
        val response = mapPointsApi.addPoint(
            requestBody = RequestBody(
                RequestPoint(
                    newPoint.coordinates,
                    if(newPoint.description == "") { null } else {newPoint.description},
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