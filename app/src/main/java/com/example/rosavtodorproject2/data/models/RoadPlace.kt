package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class RoadPlace (
    //Возможно нужно все поля сделать nullable, но тут не уверен.
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: Int,
    @SerializedName("coordinates") val coordinates: Coordinates,
    @SerializedName("distanceFromUser") val distanceFromUser: Int,
)