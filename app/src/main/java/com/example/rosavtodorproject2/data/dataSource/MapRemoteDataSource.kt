package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RequestPoint
import com.example.rosavtodorproject2.data.models.RequestPointBody
import com.example.rosavtodorproject2.ioc.AppComponentScope
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Inject

@AppComponentScope
class MapRemoteDataSource @Inject constructor(
    private val mapPointsApi: MapPointsApi
) {

    private val addedByUserPoints: MutableList<MyPoint> = mutableListOf()
    private val points: MutableList<MyPoint> = mutableListOf()
    fun loadPoints() = HttpResponseState.Success(points + addedByUserPoints)
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

                    return HttpResponseState.Success(points + addedByUserPoints)
                } else {
                    return HttpResponseState.Failure(response.message() ?: "")
                }
            },
            onFailure = {
                return HttpResponseState.Failure(it.message ?: "")
            }
        )
    }

    suspend fun addPoint(newPoint: MyPoint, reliability: Int, filePaths: List<String>) {

        addedByUserPoints.add(newPoint)

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
            ),
            files = filePaths.map {
                val file = File(it)
                val requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), file)
                MultipartBody.Part.createFormData("file", file.name, requestFile)
            }
        )

        if (response.isSuccessful) {
            //Хз надо ли вообще тут что-то проверять?
        }
    }
}