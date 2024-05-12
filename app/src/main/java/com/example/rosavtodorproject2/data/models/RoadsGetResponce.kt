package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class RoadsGetResponse(
    @SerializedName("roads") val roadsList: List<Road>
)
