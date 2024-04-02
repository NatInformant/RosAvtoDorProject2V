package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.MyPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create


class MapRemoteDataSource {

    private val BASE_URL = "https://sug4chy.un1ver5e.keenetic.link/api/Mobile/"
    private val mapPointsApi: MapPointsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MapPointsApi::class.java)
    }

    private val points: MutableList<MyPoint> = mutableListOf(
        MyPoint(
            Coordinates(54.88324475618048,57.26202530166927),
            "Азс 1.",
            4,
        ),
        MyPoint(
            Coordinates(55.52648707893606,65.22737461759895),
            "Азс 2",
            4,
        ),
        MyPoint(
            Coordinates(64.24725076450086,56.10278315937718),
            "Азс 3",
            4,
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
}