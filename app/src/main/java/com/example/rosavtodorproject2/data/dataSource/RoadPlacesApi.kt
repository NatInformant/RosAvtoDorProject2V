package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.RoadPlacesGetResponce
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoadPlacesApi {
    @GET("/api/roads/{roadName}/verifiedPoints/{type}")
    suspend fun getRoadPlaces(
        @Path("roadName") roadName: String,
        @Path("type") type: String,
        @Query("Coordinates.Latitude") latitude:Double,
        @Query("Coordinates.Longitude") longitude:Double,
        @Query("PointsCount") pointsCount:Int = 10,
        @Query("Radius") radius:Double = 100.0,
    ): Response<RoadPlacesGetResponce>
}