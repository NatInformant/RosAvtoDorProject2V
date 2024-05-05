package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.AdvertisementWithRegionName
import com.google.gson.annotations.SerializedName

data class AdvertisementsGetResponse (
    @SerializedName("advertisements") val allRegionsAdvertisements: List<AdvertisementWithRegionName>
)