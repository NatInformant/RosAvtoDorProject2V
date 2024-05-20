package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class RoadPlace (
    @SerializedName("id") val id: UUID,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: Int,
    @SerializedName("coordinates") val coordinates: Coordinates,
    @SerializedName("description") val description: String,
    @SerializedName("distanceFromUser") val distanceFromUser: Double,
)