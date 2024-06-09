package com.example.rosavtodorproject2.data.dataSource

import android.net.Uri
import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RequestPointBody
import com.example.rosavtodorproject2.data.models.RequestPoint
import com.example.rosavtodorproject2.data.models.RoadPlace
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
    private val BASE_LATITUDE: Double = 55.154
    private val BASE_LONGITUDE: Double = 61.4291
    fun loadPoints() = HttpResponseState.Success(points)
    suspend fun getPoints(
        currentLatitude: Double,
        currentLongitude: Double
    ): HttpResponseState<List<MyPoint>> {
        points.clear()
        kotlin.runCatching {
            mapPointsApi.getPoints(
                currentLatitude,
                currentLongitude,
            )
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    response.body()?.points?.forEach {
                        points.add(it)
                    }

                    return HttpResponseState.Success(points.toList())
                } else {
                    return HttpResponseState.Failure(response.message() ?: "")
                }
            },
            onFailure = {
                return HttpResponseState.Failure(it.message ?: "")
            }
        )
    }

    suspend fun addPoint(newPoint: MyPoint, reliability: Int, fileUris: List<Uri>) {



        val response = mapPointsApi.addPoint(
            requestPointBody = RequestPointBody(
                RequestPoint(
                    type = newPoint.type - 5,
                    coordinates = newPoint.coordinates,
                    description = if (newPoint.name == "") {
                        null
                    } else {
                        newPoint.name
                    },
                    /*reliability = reliability*/
                )
            )
        )

        if (response.isSuccessful) {
            //Хз надо ли вообще тут что-то проверять?
        }
    }
}