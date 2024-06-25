package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class Advertisement(
    @SerializedName("title") val title:String,
    @SerializedName("description") val description:String,
)
