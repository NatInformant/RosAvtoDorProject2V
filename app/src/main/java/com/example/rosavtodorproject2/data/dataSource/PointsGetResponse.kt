package com.example.rosavtodorproject2.data.dataSource

import com.example.rosavtodorproject2.data.models.MyPoint
import com.google.gson.annotations.SerializedName

data class PointsGetResponse(
    @SerializedName("points")val points:List<MyPoint>
)
