package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.PointPostResponse
import com.example.rosavtodorproject2.data.models.PointsGetResponse
import com.example.rosavtodorproject2.data.models.RequestPointBody
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface MapPointsApi {
    @GET("api/verifiedPoints")
    suspend fun getPoints(
        @Query("Coordinates.Latitude") latitude:Double,
        @Query("Coordinates.Longitude") longitude:Double,
        @Query("Radius") radius:Double = 100.0,
    ): Response<PointsGetResponse>
    @POST("api/unverifiedPoints")
    suspend fun addPoint(
        @Body requestPointBody: RequestPointBody,
        @Part files: List<MultipartBody.Part>
    ):Response<PointPostResponse>
}
