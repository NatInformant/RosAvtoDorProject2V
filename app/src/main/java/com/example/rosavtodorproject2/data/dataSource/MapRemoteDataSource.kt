package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RequestPoint
import com.example.rosavtodorproject2.ioc.AppComponentScope
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@AppComponentScope
class MapRemoteDataSource @Inject constructor(
    private val mapPointsApi: MapPointsApi
) {

    private val addedByUserPoints: MutableList<MyPoint> = mutableListOf()
    private val points: MutableList<MyPoint> = mutableListOf()
    fun loadPoints() = HttpResponseState.Success(points + addedByUserPoints)
    fun addPointLocally(newPoint: MyPoint) = addedByUserPoints.add(newPoint)
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

    suspend fun addPointRemote(newPoint: MyPoint, reliability: Int, filePaths: List<String>) {
        val response = mapPointsApi.addPoint(
            requestPoint = RequestPoint(
                type = newPoint.type - 5,
                coordinates = newPoint.coordinates,
                description = if (newPoint.name == "") {
                    null
                } else {
                    newPoint.name
                },
                reliability = reliability
            )
        )

        if (response.isSuccessful) {
            mapPointsApi.addPhotoToPoint(
                pointId = response.body()!!.pointId,
                Files = filePaths.map {
                    val file = File(it)
                    val requestFile =
                        file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("Files", file.name, requestFile)
                }
            )
        }

    }
}