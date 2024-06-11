package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class RequestPoint(
    //Возможно нужно все поля сделать nullable, но тут не уверен.
    @SerializedName("type") val type: Int,
    @SerializedName("coordinates") val coordinates: Coordinates,
    @SerializedName("description") val description: String?,
    @SerializedName("reliability") val reliability:Int,
)
