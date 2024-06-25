package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.AdvertisementsGetResponse
import retrofit2.Response
import retrofit2.http.GET

interface AdvertisementsApi {
    @GET("regions/advertisements")
    suspend fun getAdvertisements(): Response<AdvertisementsGetResponse>
}