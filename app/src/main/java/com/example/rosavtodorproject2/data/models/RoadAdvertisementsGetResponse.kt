package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class RoadAdvertisementsGetResponse(
    @SerializedName("advertisements") val roadAdvertisements: List<AdvertisementWithRegionName>
)
