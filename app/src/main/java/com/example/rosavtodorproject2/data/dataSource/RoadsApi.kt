package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.RoadsGetResponse
import retrofit2.Response
import retrofit2.http.GET

interface RoadsApi {
    @GET("roads")
    suspend fun getRoads(): Response<RoadsGetResponse>
}