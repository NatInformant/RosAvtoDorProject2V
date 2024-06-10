package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.Coordinates
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.Road
import com.example.rosavtodorproject2.data.models.RoadPlace
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RoadPlacesRemoteDataSource {

    private val roadsApi: RoadPlacesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoadPlacesApi::class.java)
    }

    suspend fun loadRoadPlaces(
        roadName: String,
        roadPlacesType: String,
        currentUserPosition: Coordinates
    ): HttpResponseState<List<RoadPlace>> {

        kotlin.runCatching {
            roadsApi.getRoadPlaces(
                roadName,
                roadPlacesType,
                currentUserPosition.latitude,
                currentUserPosition.longitude
            )
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    return HttpResponseState.Success( response.body()?.roadPlacesList ?: emptyList())
                } else {
                    return HttpResponseState.Failure(response.message() ?: "")
                }
            },
            onFailure = {
                return HttpResponseState.Failure(it.message ?: "")
            }
        )
    }
}