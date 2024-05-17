package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
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


    private var roadPlaces: MutableList<RoadPlace> =
        mutableListOf()

    suspend fun loadRoadPlaces(roadName:String): HttpResponseState<List<RoadPlace>> {
        roadPlaces.clear()

        kotlin.runCatching {
            roadsApi.getRoadPlaces(roadName)
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {

                    response.body()?.roadPlacesList?.forEach { roadPlace: RoadPlace ->
                        roadPlaces.add(
                            roadPlace
                        )
                    }
                    return HttpResponseState.Success(roadPlaces.toList())
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