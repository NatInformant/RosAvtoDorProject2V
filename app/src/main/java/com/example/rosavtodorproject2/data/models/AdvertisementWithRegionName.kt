package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class AdvertisementWithRegionName(
    @SerializedName("title") val title:String,
    @SerializedName("description") val description:String,
    @SerializedName("regionName") val regionName:String,
)
