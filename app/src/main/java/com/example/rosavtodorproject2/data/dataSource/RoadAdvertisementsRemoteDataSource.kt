package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.AdvertisementWithRegionName
import com.example.rosavtodorproject2.data.models.HttpResponseState
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RoadAdvertisementsRemoteDataSource {
    private val roadAdvertisementsApi: RoadAdvertisementsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RoadAdvertisementsApi::class.java)
    }


    private var roadAdvertisements: MutableList<Advertisement> =
        mutableListOf()

    suspend fun loadRoadAdvertisements(roadName:String): HttpResponseState<List<Advertisement>> {
        roadAdvertisements.clear()

        kotlin.runCatching {
            roadAdvertisementsApi.getAdvertisements(roadName)
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    response.body()?.roadAdvertisements?.forEach { advertisement: AdvertisementWithRegionName ->
                        roadAdvertisements.add(
                            Advertisement(advertisement.title, advertisement.description)
                        )
                    }
                    //toList() нужен, чтобы мы ссылку на roads в liveData не передавали, а
                    // то иначе она будет реагировать на его очистку и работать не корректно
                    return HttpResponseState.Success(
                        roadAdvertisements.toList()
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