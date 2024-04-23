package com.example.rosavtodorproject2.data.models
import com.google.gson.annotations.SerializedName
data class RequestBody (
    @SerializedName("point")val point:RequestPoint,
    @SerializedName("roadName")val roadName:String?,
)