package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class AdvertisementsGetResponse (
    @SerializedName("advertisements") val allRegionsAdvertisements: List<AdvertisementWithRegionName>
)