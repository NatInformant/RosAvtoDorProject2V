package com.example.rosavtodorproject2.data.models

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class PointPostResponse(
    @SerializedName("pointId")val pointId: UUID
)
