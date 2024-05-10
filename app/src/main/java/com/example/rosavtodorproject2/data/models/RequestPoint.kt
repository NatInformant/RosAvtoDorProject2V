package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class RequestPoint(
    //Возможно нужно все поля сделать nullable, но тут не уверен.
    @SerializedName("coordinates") val coordinates: Coordinates,
    @SerializedName("description") val description: String?,
    @SerializedName("type") val type: String,
    @SerializedName("reliability") val reliability:Int,
)
