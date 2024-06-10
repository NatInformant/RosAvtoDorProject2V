package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.Road
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RoadsRemoteDataSource {
    private val roadsApi: RoadsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoadsApi::class.java)
    }

    suspend fun loadRoads(): HttpResponseState<List<Road>> {

        kotlin.runCatching {
            roadsApi.getRoads()
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    return HttpResponseState.Success(response.body()?.roadsList ?: emptyList())
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