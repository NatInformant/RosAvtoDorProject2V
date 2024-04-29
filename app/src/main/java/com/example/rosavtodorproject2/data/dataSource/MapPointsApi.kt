package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.MyPoint
import com.example.rosavtodorproject2.data.models.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface MapPointsApi {
    @GET("roads/verifiedPoints")
    suspend fun getPoints(
        @Query("Coordinates.Latitude") latitude:Double,
        @Query("Coordinates.Longitude") longitude:Double,
        @Query("Radius") radius:Double,
    ): Response<GetResponse>
    @POST("unverifiedPoints")
    suspend fun addPoint(
        @Body requestBody: RequestBody,
    ):Response<PostResponse>
}
