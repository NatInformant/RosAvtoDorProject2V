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


    private var roads: MutableList<Road> =
        mutableListOf()

    suspend fun loadRoads(): HttpResponseState<List<Road>> {
        roads.clear()

        kotlin.runCatching {
            roadsApi.getAdvertisements()
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {

                    response.body()?.roadsList?.forEach { road: Road ->
                        roads.add(
                            road
                        )
                    }

                    return HttpResponseState.Success(roads)
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