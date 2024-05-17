package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class Coordinates(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
)