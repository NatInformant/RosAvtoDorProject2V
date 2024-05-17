package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName

data class RoadPlacesGetResponce(
    @SerializedName("points") val roadPlacesList: List<RoadPlace>
)