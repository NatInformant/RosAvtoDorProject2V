package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.HttpResponseState
import com.example.rosavtodorproject2.data.models.Road
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject

class RoadsRemoteDataSource @Inject constructor(
    private val roadsApi: RoadsApi
) {

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