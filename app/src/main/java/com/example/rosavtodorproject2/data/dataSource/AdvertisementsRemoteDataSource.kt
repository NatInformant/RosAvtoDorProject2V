package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.AdvertisementWithRegionName
import com.example.rosavtodorproject2.data.models.HttpResponseState
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.SortedMap

class AdvertisementsRemoteDataSource {
    private val advertisementsApi: AdvertisementsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdvertisementsApi::class.java)
    }


    private var advertisementsWithRegionNames: MutableList<AdvertisementWithRegionName> =
        mutableListOf()

    suspend fun loadAdvertisements(): HttpResponseState<List<Pair<String, List<Advertisement>>>> {
        advertisementsWithRegionNames.clear()

        kotlin.runCatching {
            advertisementsApi.getAdvertisements()
        }.fold(
            onSuccess = { response ->
                if (response.isSuccessful) {
                    response.body()?.allRegionsAdvertisements?.forEach { advertisement: AdvertisementWithRegionName ->
                        advertisementsWithRegionNames.add(
                            advertisement
                        )
                    }
                    return HttpResponseState.Success(
                        advertisementsWithRegionNames.groupBy(
                            keySelector = { it.regionName },
                            valueTransform = { Advertisement(it.title, it.description) }
                        ).toSortedMap().toList()
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