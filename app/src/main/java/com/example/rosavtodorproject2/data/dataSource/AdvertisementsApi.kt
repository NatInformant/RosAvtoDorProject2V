package com.example.rosavtodorproject2.data.dataSource

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface AdvertisementsApi {
    @GET("regions/advertisements")
    suspend fun getAdvertisements(): Response<AdvertisementsGetResponse>
}