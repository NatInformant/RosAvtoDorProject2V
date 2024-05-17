package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.RoadPlacesGetResponce
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RoadPlacesApi {
    @GET("/api/roads/{roadName}/verifiedPoints/{type}")
    suspend fun getRoadPlaces(@Path("roadName") roadName:String, ): Response<RoadPlacesGetResponce>
}