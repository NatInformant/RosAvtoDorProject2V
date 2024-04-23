package com.example.rosavtodorproject2.data.models
import com.google.gson.annotations.SerializedName
data class RequestBody (
    @SerializedName("point")val point:MyPoint,
    @SerializedName("roadName")val roadName:String,
)