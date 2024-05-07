package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.BuildConfig
import com.example.rosavtodorproject2.data.models.Advertisement
import com.example.rosavtodorproject2.data.models.AdvertisementWithRegionName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.SortedMap

class AdvertisementsRemoteDataSource {
    /*private val advertisementsWithRegionNames: List<AdvertisementWithRegionName> = listOf(
        AdvertisementWithRegionName(
            "Буран",
            "В области страшный буран, бегите глубцы, а то оно вас сожрёт и не подавиться",
            "Челябинская область"
        ),
        AdvertisementWithRegionName(
            "Снегопад",
            "В области страшный снегопад, на дорогах больше метра снега, я вообще ХЗ зачем вы из дома вышли",
            "Челябинская область"
        ),
        AdvertisementWithRegionName(
            "Дорог нет",
            "Все дороги были срыты под ноль, т.к. а ХЗ зачем они были вообще нужны",
            "Свердловская область"
        ),
        AdvertisementWithRegionName(
            "В Кургане чума",
            "В Кургане реально бубонная чума, БЕГИТЕ ВСЕ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!",
            "Курганская область"
        ),
        AdvertisementWithRegionName(
            "В Курганской области потом",
            "В Курганской области, все дороги размыло, введено черезвычайное положение, не пытайтесь даже ехать сюда.",
            "Курганская область"
        ),
    )*/
    private val advertisementsApi: AdvertisementsApi by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AdvertisementsApi::class.java)
    }


    private var advertisementsWithRegionNames: MutableList<AdvertisementWithRegionName> =
        mutableListOf()

    suspend fun loadAdvertisements() :SortedMap<String, List<Advertisement>> {
        advertisementsWithRegionNames.clear()
        val response = advertisementsApi.getAdvertisements()
        if (response.isSuccessful) {
            response.body()?.allRegionsAdvertisements?.forEach {
                advertisementsWithRegionNames.add(
                    it
                )
            }
        }

        return advertisementsWithRegionNames.groupBy(keySelector = { it.regionName },
            valueTransform = { Advertisement(it.title, it.description) }).toSortedMap()
    }
}