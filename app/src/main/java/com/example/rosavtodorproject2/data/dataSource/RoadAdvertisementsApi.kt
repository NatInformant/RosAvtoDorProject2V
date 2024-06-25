package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.RoadAdvertisementsGetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RoadAdvertisementsApi {
    @GET("roads/{roadName}/advertisements")
    suspend fun getAdvertisements(@Path("roadName") roadName:String): Response<RoadAdvertisementsGetResponse>
}