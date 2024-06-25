package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class RoadPlace (
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: Int,
    @SerializedName("coordinates") val coordinates: Coordinates,
    @SerializedName("description") val description: String,
    @SerializedName("distanceFromUser") val distanceFromUser: Double,
)