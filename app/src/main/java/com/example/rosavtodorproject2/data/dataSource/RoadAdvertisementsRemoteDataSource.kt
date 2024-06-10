package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.AdvertisementWithRegionName
import com.example.rosavtodorproject2.data.models.HttpResponseState
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RoadAdvertisementsRemoteDataSource @Inject constructor(
    private val roadAdvertisementsApi: RoadAdvertisementsApi
) {

    suspend fun loadRoadAdvertisements(roadName: String): HttpResponseState<List<Advertisement>> {

        kotlin.runCatching {
            roadAdvertisementsApi.getAdvertisements(roadName)
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    return HttpResponseState.Success(
                        response.body()?.roadAdvertisements?.map {
                            Advertisement(
                                it.title,
                                it.description
                            )
                        } ?: emptyList()
                    )
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